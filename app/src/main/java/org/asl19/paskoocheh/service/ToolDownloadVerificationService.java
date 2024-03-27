package org.asl19.paskoocheh.service;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.aefyr.sai.installer2.impl.rootless.RootlessSaiPiBroadcastReceiver;
import com.aefyr.sai.viewmodels.InstallerXDialogViewModel;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.utils.Checksum;
import org.asl19.paskoocheh.utils.FileViewer;
import org.parceler.Parcels;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.asl19.paskoocheh.Constants.ASC;
import static org.asl19.paskoocheh.Constants.AUTHORITY;
import static org.asl19.paskoocheh.Constants.TEMP;
import static org.asl19.paskoocheh.utils.PGPUtil.verifySignature;

public class ToolDownloadVerificationService extends IntentService {
    private final String TAG = "DownloadVerificationService";
    private Version version;

    private ApkManager apkManager;
    private InstallerXDialogViewModel mViewModel;
    private String EXTRA_VERIFICATION_SUCCESSFUL = "extra_verification_successful";
    public static String EXTRA_APP_VERIFICATION_FAILED = "extra_verification_successful";
    public static final String EXTRA_DOWNLOAD_AND_VERIFICATION_SUCCESSFUL = "extra_download_and_verification_successful";

    public ToolDownloadVerificationService() {
        super("ToolDownloadVerificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        apkManager = new ApkManager(getApplicationContext());
        mViewModel = new InstallerXDialogViewModel(this, null);
    }

    private void sendNonZipFileDownloadAndVerificationSuccessIntent() {
        Intent intent = new Intent();
        intent.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);
        intent.putExtra(EXTRA_DOWNLOAD_AND_VERIFICATION_SUCCESSFUL, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setOngoing(true);

        try {
            this.version = Parcels.unwrap(intent.getExtras().getParcelable(InstallFragment.VERSION));
            Integer urlSplit = version.getS3Key().lastIndexOf("/");
            String externalFile = version.getS3Key().substring(urlSplit + 1);
            String[] tmp = externalFile.split("\\.");
            String fileExtension = tmp[tmp.length-1];

            notificationBuilder.setContentTitle(version.getAppName());

            final File internalTempFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension + TEMP, version.getAppName(), version.getVersionNumber()));
            final File internalSecurityFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension + ASC, version.getAppName(), version.getVersionNumber()));

            boolean isVerificationSuccessful = intent.getBooleanExtra(EXTRA_VERIFICATION_SUCCESSFUL, false);

            if (isVerificationSuccessful) {
                ToolDownloadService.toast(getApplicationContext(), getString(R.string.app_installation_in_progress), version.getAppName());
            }

            notificationBuilder.setContentTitle(version.getAppName());
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setProgress(0, 0, true);
            notificationBuilder.setContentText(getString(R.string.verifying));

            startForeground(version.getToolId(), notificationBuilder.build());

            if (isVerificationSuccessful || (version.getChecksum().isEmpty() || Checksum.checkChecksum(version.getChecksum(), internalTempFile))
                    && verifySignature(
                    new BufferedInputStream(new FileInputStream(internalTempFile)),
                    new BufferedInputStream(new FileInputStream(internalSecurityFile)),
                    new BufferedInputStream(getApplicationContext().getAssets().open("public_key.pub"))
            )) {
                Log.d(TAG, "file verification success");
                File internalFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension, version.getAppName(), version.getVersionNumber()));
                internalTempFile.renameTo(internalFile);
                internalTempFile.setReadable(true, false);

                Uri internalUri = Uri.fromFile(internalFile);
                if (Build.VERSION.SDK_INT >= 24) {
                    internalUri = FileProvider.getUriForFile(getApplicationContext(), AUTHORITY, internalFile);
                }

                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(internalUri, "application/vnd.android.package-archive");

                List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(installIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getApplicationContext().grantUriPermission(packageName, internalUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                PendingIntent pendingIntent = null;
                if (fileExtension.equals("zip")) {
                    intent.putExtra(EXTRA_VERIFICATION_SUCCESSFUL, true);
                    pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    pendingIntent = PendingIntent.getActivity(
                            getApplicationContext(), 0, installIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                notificationManager.cancel(version.getToolId());

                if (!fileExtension.equals("zip")) {
                    sendNonZipFileDownloadAndVerificationSuccessIntent();
                }

                if (fileExtension.equals("apk")) {
                    apkManager.installPackage(version, internalFile);
                } else if(fileExtension.equals("zip")) {
                    // For zip or other AAB formats, use the open source SAI AAB installer code to extract the APK and install it. The code is inside InstallAABService.
                    Intent installAABServiceIntent = new Intent(this, InstallAABService.class);
                    installAABServiceIntent.putExtras(intent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(installAABServiceIntent);
                    } else {
                        startService(installAABServiceIntent);
                    }
                } else {
                    FileViewer.viewFile(getApplicationContext(), internalFile, fileExtension);
                }
            } else {
                Log.d(TAG, "file verification FAIL");
                ToolDownloadService.toast(getApplicationContext(), getString(R.string.checksum_invalid), version.getAppName());

                for (File file : new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                    if (file.getName().startsWith(version.getAppName())) {
                        file.delete();
                    }
                }
                FirebaseCrashlytics.getInstance().log(String.format(getString(R.string.checksum_invalid), version.getAppName()));
                sendVerificationErrorIntent(null);
                stopForeground(true); // Also hide the system tray notification.
            }
        } catch (Exception exception) {
            Log.d(TAG, "file verification EXCEPTION = " + exception.getStackTrace().toString());
            for (File file: new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                if (file.getName().startsWith(version.getAppName())) {
                    file.delete();
                }
            }

            Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
            notificationIntent.putExtra("VERSION", Parcels.wrap(version));
            PendingIntent pendingIntent = PendingIntent.getService(
                    getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Log.e(this.getClass().getSimpleName(), exception.toString());

            sendVerificationErrorIntent(exception);

            stopForeground(false);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setContentText(getString(R.string.download_failed_retry));
            notificationBuilder.setProgress(0, 0, false);
            notificationManager.notify(version.getToolId(), notificationBuilder.build());
        }
    }

    private void  sendVerificationErrorIntent(Exception exception) {
        Log.d(TAG, "sendVerificationErrorIntent");
        Intent intent = new Intent();
        intent.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);

        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_IS_INSTALL_SUCCESS, false);
        intent.putExtra(EXTRA_APP_VERIFICATION_FAILED, true);

        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_PACKAGE_NAME, version.appName);
        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_FULL_ERROR, getString(R.string.verification_failed_dialog_error_message));

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        if (exception != null) {
            FirebaseCrashlytics.getInstance().recordException(exception);
        }
        FirebaseCrashlytics.getInstance().log("Failed to install " + version.appName + "; Error = " + getString(R.string.verification_failed_dialog_error_message));
    }
}
