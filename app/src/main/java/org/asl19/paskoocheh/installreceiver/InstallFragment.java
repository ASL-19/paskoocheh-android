package org.asl19.paskoocheh.installreceiver;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.aefyr.sai.installer2.impl.rootless.RootlessSaiPiBroadcastReceiver;
import com.aefyr.sai.ui.dialogs.AppInstalledDialogFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.OuinetService;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.AmazonContentBodyRequest;
import org.asl19.paskoocheh.data.AmazonReportDeviceRequest;
import org.asl19.paskoocheh.data.source.AmazonDataSource;
import org.asl19.paskoocheh.data.source.AmazonRepository;
import org.asl19.paskoocheh.dialogs.AppNotInstallableDialog;
import org.asl19.paskoocheh.dialogs.PaskoochehDialog;
import org.asl19.paskoocheh.pojo.AppDownloadInfoForVersionCode;
import org.asl19.paskoocheh.pojo.DeviceInfo;
import org.asl19.paskoocheh.pojo.DownloadVia;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.service.InstallAABService;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.service.ToolDownloadVerificationService;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.utils.FileViewer;
import org.parceler.Parcels;

import java.io.File;


public abstract class InstallFragment extends Fragment {
    public static final String TAG = InstallFragment.class.getCanonicalName();

    public static final String INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS = "app_download_install_status";
    public static final int REQUEST_CODE_CONFIRM_INSTALLATION = 322;
    public static final String VERSION = "VERSION";
    public static final String TOOL = "TOOL";
    private static final String EXTRA_IS_INSTALLATION_IN_PROGRESS = "EXTRA_IS_INSTALLATION_IN_PROGRESS";
    private static final String EXTRA_LAST_INSTALL_TIMESTAMP = "EXTRA_LAST_INSTALL_TIMESTAMP";
    private static final int MINIMUM_WAIT_TIME_BETWEEN_TWO_INSTALLS = 30000; // This is just so that in case if an install takes forever, we allow next install to take place to avoid permanent wait.

    private ApkManager apkManager;

    private PaskoochehDialog dialogAppInstallProgress;
    private boolean userAbortedInstallOrInstallFailed = false;

    private boolean isInstallInProgress = false;
    private long lastInstallTimestamp = 0;

    protected boolean saveInstanceStateComplete;

    protected int versionId;
    protected Version version;

    public InstallFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate savedInstanceState is not null");
            this.version = Parcels.unwrap(savedInstanceState.getParcelable(VERSION));
            this.versionId = savedInstanceState.getInt(TOOL);
            this.isInstallInProgress = savedInstanceState.getBoolean(EXTRA_IS_INSTALLATION_IN_PROGRESS, false);
            this.lastInstallTimestamp = savedInstanceState.getLong(EXTRA_LAST_INSTALL_TIMESTAMP, 0);
        }
        apkManager = new ApkManager(getContext().getApplicationContext());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAppDownloadAndInstallStatusReceiver, new IntentFilter(INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS));
    }

    @Override
    public void onResume() {
        super.onResume();
        saveInstanceStateComplete = false;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveInstanceStateComplete = true;
        if (version != null) {
            bundle.putInt(TOOL, versionId);
            bundle.putParcelable(VERSION, Parcels.wrap(version));
            bundle.putBoolean(EXTRA_IS_INSTALLATION_IN_PROGRESS, isInstallInProgress);
            bundle.putLong(EXTRA_LAST_INSTALL_TIMESTAMP, lastInstallTimestamp);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(saveInstanceStateComplete) { // TODO: jay - in case if goes to background store the dialog and then show when onResume is called again.
            Log.d(TAG, "onActivityResult saveInstanceStateComplete = true");
            return;
        }

        if (requestCode == REQUEST_CODE_CONFIRM_INSTALLATION && !userAbortedInstallOrInstallFailed) {
            Log.d(TAG, "onActivityResult - showing Paskooceh dialog userAbuserAbortedInstallOrInstallFailed = " + userAbortedInstallOrInstallFailed);
            dialogAppInstallProgress = new PaskoochehDialog(getString(R.string.please_wait), getActivity().getColor(android.R.color.black),
                    getString(R.string.app_installation_in_progress, version.appName), true);
            dialogAppInstallProgress.show(getChildFragmentManager(), "app_installing_dialog");
        } else if(requestCode == 0) {
            if (getActivity().getPackageManager().canRequestPackageInstalls()) {
                Log.d(TAG, "onActivityResult - canRequestPackageInstalls = granted version =" +  version);
                installS3(FileViewer.getFileExtension(version));
            } else {
                Log.d(TAG, "onActivityResult - canRequestPackageInstalls = NOT granted");
            }
        }
    }

    private BroadcastReceiver mAppDownloadAndInstallStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mAppDownloadAndInstallStatusReceiver.onReceive calling setIsInstallInProgress(false)");
            setIsInstallInProgress(false);

            boolean isNonZipFileDownloadAndVerificationComplete = intent.getBooleanExtra(ToolDownloadVerificationService.EXTRA_DOWNLOAD_AND_VERIFICATION_SUCCESSFUL, false);
            if (isNonZipFileDownloadAndVerificationComplete) {
                return;
            }

            if(saveInstanceStateComplete) { // TODO: jay - in case if goes to background store the dialog and then show when onResume is called again.
                return;
            }

            boolean isShowUserInstallDialog = intent.getBooleanExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_IS_SHOW_USER_INSTALL_DIALOG, false);

            if (isShowUserInstallDialog) {
                Log.d(TAG, "onReceive userAbortedInstallOrInstallFailed = " + userAbortedInstallOrInstallFailed);
                Intent mConfirmationIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
                mConfirmationIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                userAbortedInstallOrInstallFailed = false;
                try {
                    startActivityForResult(mConfirmationIntent, REQUEST_CODE_CONFIRM_INSTALLATION);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

                return;
            }

            userAbortedInstallOrInstallFailed = intent.getBooleanExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_USER_ABORTED, false);
            Log.d(TAG, "onReceive userAbortedInstallOrInstallFailed = " + userAbortedInstallOrInstallFailed);

            if (userAbortedInstallOrInstallFailed) {
                // i.e. User pressed Cancel button on  the install dialog presented by the Android PackageInstaller library.
                if (dialogAppInstallProgress != null) {
                    Log.d(TAG, "onReceive userAbortedInstallOrInstallFailed = " + userAbortedInstallOrInstallFailed + " therefore dismissing the spinner");
                    // There is a bug in the PackageInstaller which will send resultCode = RESULT_CANCELLED even though an user accepted the Install dialog.
                    // Due to this in the onActivityResult the dialogAppInstallProgress is shown.
                    dialogAppInstallProgress.dismiss();
                    dialogAppInstallProgress = null;
                } else {
                    Log.d(TAG, "onReceive dialogAppInstallProgress = null");
                }
                cancelInstallServiceNotification();
                onInstallFailureUIUpdate();
                return;
            }

            String packageName = intent.getStringExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_PACKAGE_NAME);
            boolean isSuccess = intent.getBooleanExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_IS_INSTALL_SUCCESS, false);
            Log.d(TAG, "onReceive isSuccess = " + isSuccess);

            if (packageName == null) {
                packageName = version.getAppName();
            }

            if (dialogAppInstallProgress != null) {
                Log.d(TAG, "onReceive dialogAppInstallProgress!=null so dismiss  dialogAppInstallProgress");
                dialogAppInstallProgress.dismiss();
                dialogAppInstallProgress = null;
            }

            if (isSuccess) {
                AppInstalledDialogFragment.newInstance(packageName, version.appName, null).show(InstallFragment.this.getChildFragmentManager(), "dialog_app_installed");
                onInstallSuccessUIUpdate();
                cancelInstallServiceNotification();
            } else {
                userAbortedInstallOrInstallFailed = true; // This is needed so that if onReceive is called due to failure before onActivityResult is called by Android's package installer after user clicks install button on the dialog presented by the Android's package installer, we do not show install dialog spinner.
                onInstallFailureUIUpdate();
                boolean p2pDownloadFailed = intent.getBooleanExtra(ToolDownloadService.INTENT_EXTRA_P2P_DOWNLOAD_FAILED, false);
                if (p2pDownloadFailed && PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
                    Log.d(TAG, "onReceive showP2PAppNotFoundInCacheAlert");
                    PaskoochehApplication.getInstance().getP2PAlerts().showP2PAppNotFoundInCacheAlert(InstallFragment.this.getChildFragmentManager());
                } else {
                    cancelInstallServiceNotification();
                    String shortError = intent.getStringExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_SHORT_ERROR);
                    String fullError = intent.getStringExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_FULL_ERROR);
                    boolean isToolVerificationFail = intent.getBooleanExtra(ToolDownloadVerificationService.EXTRA_APP_VERIFICATION_FAILED, false);
                    if (!isToolVerificationFail) {
                        fullError = getString(R.string.installer_app_installation_failure);
                    }
                    Log.d(TAG, "onReceive show error =" + fullError);
                    AppInstalledDialogFragment.newInstance(packageName, version.appName, fullError).show(InstallFragment.this.getChildFragmentManager(), "dialog_app_installation_fail");
                }
            }
        }
    };

    protected abstract void onInstallSuccessUIUpdate();
    protected abstract void onInstallFailureUIUpdate();

    private void processNonAppFile(String fileExtension) {
        Log.d(TAG, "processNonAppFile fileExtension=" + fileExtension);
        File toolFile = new File(getContext().getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension, version.getAppName(), version.getVersionNumber()));
        if (toolFile.exists()) {
            FileViewer.viewFile(getContext(), toolFile, fileExtension);
        } else {
            downloadFileAndThenProcess();
        }
    }

    private AlertDialog alert = null;
    private void requestInstallPermissionIfNotGrantedByTheUser(String fileExtension) {
        if (getActivity().getPackageManager().canRequestPackageInstalls()) {
            installS3(fileExtension);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.install_unknown_apps_settings_dialog_description))
                    .setNegativeButton(R.string.cancel, null);

            builder.setPositiveButton(R.string.settings, (d, w) -> {
                try {
                    startActivityForResult(new Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            android.net.Uri.parse("package:" + BuildConfig.APPLICATION_ID)), 0);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Unable to launch settings activity for ACTION_MANAGE_UNKNOWN_APP_SOURCES");
                }
                alert.dismiss();
            });

            alert = builder.create();
            alert.show();
        }
    }

    private void setIsInstallInProgress(boolean isInstallInProgress) {
        Log.d(TAG, "setIsInstallInProgress = " + isInstallInProgress);
        if (isInstallInProgress) {
            this.isInstallInProgress = true;
            this.lastInstallTimestamp = System.currentTimeMillis();
        } else {
            this.isInstallInProgress = false;
            this.lastInstallTimestamp = 0;
        }
    }

    protected void installApplication(Version version) {
        long currentTimeInMilliSecs = System.currentTimeMillis();
        Log.d(TAG, "installApplication isInstallInProgress=" + isInstallInProgress + ". Wait between two installs = " + (currentTimeInMilliSecs - lastInstallTimestamp));
        if (isInstallInProgress && (currentTimeInMilliSecs - lastInstallTimestamp) < MINIMUM_WAIT_TIME_BETWEEN_TWO_INSTALLS) {
            Toast.makeText(getContext(), String.format(getContext().getString(R.string.wait_for_download_install_complete_toast_message), this.version.appName), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "installApplication calling setIsInstallInProgress(false) for version.downloadVia.s3 = " +  version.getDownloadVia().getS3());
        setIsInstallInProgress(false);

        this.version = version;

        boolean isAppInstallableInThisDevice = checkAndUpdateVersionObjectIfTheAppIsInstallableInThisDevice(this.version, getContext().getPackageName());

        DownloadVia downloadVia = version.getDownloadVia();

        String s3Url = downloadVia.getS3();
        String s3FileExtension = s3Url != null && s3Url.startsWith("https://s3.amazonaws.com/" + Constants.BUCKET_NAME) ? FileViewer.getFileExtension(version) : null;
        if (s3FileExtension != null && !s3FileExtension.isEmpty() ) {
            if (version.isInstallable) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requestInstallPermissionIfNotGrantedByTheUser(s3FileExtension);
                } else {
                    installS3(FileViewer.getFileExtension(version));
                }
            } else {
                processNonAppFile(s3FileExtension);
            }
        } else {
            if (version.isInstallable) {
                if (!isAppInstallableInThisDevice) {
                    // Display a dialog saying that the App is not downloadable in the device and to try installing the App from the Google PlayStore.
                    showAppNotInstallableDialog();
                }
            } else {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(version.getDownloadVia().getUrl())
                );

                startActivity(browserIntent);
            }
        }
    }

    private void reportAppAndDeviceToTheServer() {
        // Report the device name, hardware name, and the App name to the server so that the server can download a version of the App from the PlayStore that can be installed in this device.
        Bundle eventBundle = new Bundle();
        Log.d(TAG, "App =" + version.appName +  ". Device = " + Build.DEVICE + "; HARDWARE = " + Build.HARDWARE);
        eventBundle.putString("Device", Build.DEVICE);
        eventBundle.putString("Hardware", Build.HARDWARE);
        eventBundle.putString("Model", Build.MODEL);
        eventBundle.putString("Product", Build.PRODUCT);

        AmazonReportDeviceRequest amazonReportDeviceRequest = new AmazonReportDeviceRequest(getActivity().getApplicationContext());
        amazonReportDeviceRequest.setType(AmazonContentBodyRequest.REPORT_DEVICE_APP_NOT_INSTALLABLE);
        amazonReportDeviceRequest.setId(Build.ID);
        amazonReportDeviceRequest.setDevice(Build.DEVICE);
        //amazonReportDeviceRequest.setDevice("test-device-delete-it-10000");// TODO jay uncomment to test device reporting API.
        amazonReportDeviceRequest.setHardware(Build.HARDWARE);
        amazonReportDeviceRequest.setProduct(Build.PRODUCT);
        amazonReportDeviceRequest.setModel(Build.MODEL);
        amazonReportDeviceRequest.setRadio(Build.RADIO);
        amazonReportDeviceRequest.setBootloader(Build.BOOTLOADER);
        amazonReportDeviceRequest.setFingerprint(Build.FINGERPRINT);
        amazonReportDeviceRequest.setBrand(Build.BRAND);
        amazonReportDeviceRequest.setVersionSdk(Build.VERSION.SDK_INT);
        amazonReportDeviceRequest.setManufacturer(Build.MANUFACTURER);
        amazonReportDeviceRequest.setVersionRelease(Build.VERSION.RELEASE);

        AmazonRepository amazonRepository = new AmazonRepository(getActivity().getApplicationContext());
        amazonRepository.onSubmitRequest(amazonReportDeviceRequest, new AmazonDataSource.SubmitRequestCallback() {
            @Override
            public void onSubmitRequestSuccessful() {
                FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.REPORT_DEVICE_APP_NOT_INSTALLABLE_SUCCESS, eventBundle);
                //Toast.makeText(getContext(), "Report of device successful", Toast.LENGTH_LONG).show(); // TODO jay uncomment to test device reporting API.
            }

            @Override
            public void onSubmitRequestFailed() {
                FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.REPORT_DEVICE_APP_NOT_INSTALLABLE_FAILED, eventBundle);
                //Toast.makeText(getContext(), "Report of device failed", Toast.LENGTH_LONG).show(); // TODO jay uncomment to test device reporting API.
            }
        });
    }

    private void showAppNotInstallableDialog() {
        // Display a dialog saying that the App is not downloadable in the device and to try installing the App from the Google PlayStore.
        AppNotInstallableDialog appNotInstallableDialog = AppNotInstallableDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppNotInstallableDialog.EXTRA_APP, Parcels.wrap(version));
        appNotInstallableDialog.setArguments(bundle);
        appNotInstallableDialog.show(getActivity().getSupportFragmentManager(), appNotInstallableDialog.getClass().getName());
    }

    /**
     * Checks if the App specified in version is installable in this device. It also sends Firebase Analytics event if the App is not installable in this device.
     * @param version App object
     * @return true if the App is installable in this device, else false
     */
    protected boolean checkAndUpdateVersionObjectIfTheAppIsInstallableInThisDevice(Version version, String paskoochehPackageName) {
        boolean isInstallableInThisDevice = false;

        if (version.isInstallable) {
            Integer versionCode = 0; // if no device found, then use versionCode = 0 if it exists in the version_codes list.

            // Get the version code for the apk that is installable in this device.
            if (version.devices != null) {
                for (DeviceInfo device : version.devices) {
                    if (device.deviceName.toLowerCase().equals(Build.DEVICE.toLowerCase()) || device.deviceName.toLowerCase().equals(Build.HARDWARE.toLowerCase())) {// TODO jay check if toLowerCase is needed since it is starting API level 29
                        versionCode = device.versionCode;
                        break;
                    }
                }
            }

            if (version.appDownloadInfoForVersionCodes != null) {
                for (AppDownloadInfoForVersionCode appDownloadInfoForVersionCode : version.appDownloadInfoForVersionCodes) {
                    if (versionCode.equals(appDownloadInfoForVersionCode.versionCode)) {
                        version.setCurrentAppDownloadInfo(appDownloadInfoForVersionCode);
                        isInstallableInThisDevice = true;
                        break;
                    }
                }
            }

            // If we couldn't find version_code for  this device or if version_code = 0 is absent, then use the root level downloadInfo.
            if (version.downloadVia.s3 != null && !version.downloadVia.s3.isEmpty()) {
                isInstallableInThisDevice = true;
            }
        }

        if (version.isInstallable && !isInstallableInThisDevice) {
            // Report the device Build info to the server so that the server can download a version of the App from the PlayStore that can be installed in this device.
            reportAppAndDeviceToTheServer();
        }

        return isInstallableInThisDevice;
    }

    protected boolean isGooglePlayStoreUrl(Version version) {
        if (version == null || version.getDownloadVia().getUrl().isEmpty()) {
            return false;
        } else if(version.getDownloadVia().getUrl().contains("play.google")) {
            return true;
        } else {
            return false;
        }
    }

    private void cancelInstallServiceNotification() {
        Log.d(TAG, "cancelInstallServiceNotification");
        try {
            final NotificationManager notificationManager
                    = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(versionId);
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }

    public void playStoreRedirect(Version version) {
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(version.getDownloadVia().getUrl())
        );
        if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            getContext().startActivity(browserIntent);

            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
            bundle.putString(TOOL_ID, version.getAppName());
            FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.PLAY_STORE, bundle);
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_playstore_clients), Toast.LENGTH_SHORT).show();
        }
    }

    private void installS3(String fileExtension) {
        Log.d(TAG, "installS3 version = " + version);

        File toolFile = new File(getContext().getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension, version.getAppName(), version.getVersionNumber()));
        if (toolFile.exists()) {
            if (fileExtension.equals("apk")) {
                Log.d(TAG, "installS3 fileExtension= apk so calling apkManager.installPackage");
                apkManager.installPackage(version, toolFile);
            } else {
                Log.d(TAG, "installS3 calling setIsInstallInProgress(true) fileExtension="+fileExtension);
                setIsInstallInProgress(true);
                Toast.makeText(getContext(), getString(R.string.queued), Toast.LENGTH_LONG).show();
                Intent installAABServiceIntent = new Intent(getActivity(), InstallAABService.class);
                installAABServiceIntent.putExtra(VERSION, Parcels.wrap(version));
                getContext().startService(installAABServiceIntent);
            }
        } else {
            downloadFileAndThenProcess();
        }
    }

    private void downloadFileAndThenProcess() {
        ConnectivityManager connManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if (!getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
            Log.d(TAG, "downloadFileAndThenProcess proper network starting ToolDownloadService. Calling setIsInstallInProgress(true)");
            setIsInstallInProgress(true);
            Intent intent = new Intent(getActivity(), ToolDownloadService.class);
            intent.putExtra(VERSION, Parcels.wrap(version));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(intent);
            } else {
                getActivity().startService(intent);
            }
            Toast.makeText(getContext(), getString(R.string.queued), Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "downloadFileAndThenProcess connect to wifi please");
            Toast.makeText(getContext(), getString(R.string.connect_wifi), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAppDownloadAndInstallStatusReceiver);
    }
}
