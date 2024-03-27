package com.aefyr.sai.installer2.impl.rootless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.aefyr.sai.installer2.base.model.AndroidPackageInstallerError;
import com.aefyr.sai.utils.Utils;

import java.util.HashSet;

public class RootlessSaiPiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "RootlessSaiPiBR";

    public static final String ANDROID_PM_EXTRA_LEGACY_STATUS = "android.content.pm.extra.LEGACY_STATUS";

    public static final String ACTION_DELIVER_PI_EVENT = BuildConfig.APPLICATION_ID + ".action.RootlessSaiPiBroadcastReceiver.ACTION_DELIVER_PI_EVENT";

    public static final int STATUS_BAD_ROM = -322;

    public static final String INTENT_EXTRA_PACKAGE_NAME = "intent_package_name";

    public static final String INTENT_EXTRA_IS_INSTALL_SUCCESS = "intent_is_install_success";
    public static final String INTENT_EXTRA_SHORT_ERROR = "intent_short_error";
    public static final String INTENT_EXTRA_FULL_ERROR = "intent_full_error";
    public static final String INTENT_EXTRA_IS_SHOW_USER_INSTALL_DIALOG = "show_user_dialog";

    public static final String INTENT_EXTRA_USER_ABORTED = "intent_extra_user_aborted";

    private Context mContext;

    private HashSet<EventObserver> mObservers = new HashSet<>();

    public RootlessSaiPiBroadcastReceiver(Context c) {
        mContext = c.getApplicationContext();
    }

    public void addEventObserver(EventObserver observer) {
        mObservers.add(observer);
    }

    public void removeEventObserver(EventObserver observer) {
        mObservers.remove(observer);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -999);
        Log.d(TAG, "onReceive status = " + status);
        switch (status) {
            case PackageInstaller.STATUS_PENDING_USER_ACTION:
                Log.d(TAG, "Requesting user confirmation for installation");
                dispatchOnConfirmationPending(intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1), intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME));
                Intent confirmationIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
                Intent intentStatus = new Intent();
                intentStatus.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);
                intentStatus.putExtra(INTENT_EXTRA_IS_SHOW_USER_INSTALL_DIALOG, true);
                intentStatus.putExtra(Intent.EXTRA_INTENT, confirmationIntent);
                intentStatus.putExtra(PackageInstaller.EXTRA_SESSION_ID, intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1));
                //ConfirmationIntentWrapperActivity2.start(context, intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1), confirmationIntent);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intentStatus);
                break;
            case PackageInstaller.STATUS_SUCCESS:
                Log.d(TAG, "Installation succeed");
                dispatchOnInstallationSucceeded(intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1), intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME));
                break;
            case PackageInstaller.STATUS_FAILURE_ABORTED:
                Log.d(TAG, "User declined install");
                // User cancelled the install dialog.
                dispatchOnInstallationFailed(true);
                break;
            default:
                Log.d(TAG, "Installation failed");
                dispatchOnInstallationFailed(false, intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1),
                        intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME), parseError(intent), getRawError(intent), null);
                break;
        }
    }

    private void dispatchOnConfirmationPending(int sessionId, @Nullable String packageName) {
        for (EventObserver observer : mObservers)
            observer.onConfirmationPending(sessionId, packageName);
    }

    private void dispatchOnInstallationSucceeded(int sessionId, String packageName) {
        Intent intent = new Intent();
        intent.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);
        intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, packageName);
        intent.putExtra(INTENT_EXTRA_IS_INSTALL_SUCCESS, true);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        for (EventObserver observer : mObservers)
            observer.onInstallationSucceeded(sessionId, packageName);
    }

    private void dispatchOnInstallationFailed(boolean userAbortedInstall) {
        dispatchOnInstallationFailed(userAbortedInstall, 0, null, null, null, null);
    }

    private void dispatchOnInstallationFailed(boolean userAbortedInstall, int sessionId, String packageName, String shortError, @Nullable String fullError, @Nullable Exception exception) {
        Intent intent = new Intent();
        intent.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);
        intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, packageName);
        intent.putExtra(INTENT_EXTRA_IS_INSTALL_SUCCESS, false);

        if (userAbortedInstall) {
            intent.putExtra(INTENT_EXTRA_USER_ABORTED, userAbortedInstall);
        } else {
            intent.putExtra(INTENT_EXTRA_SHORT_ERROR, shortError);
            intent.putExtra(INTENT_EXTRA_FULL_ERROR, fullError);
            FirebaseCrashlytics.getInstance().recordException(exception);
            FirebaseCrashlytics.getInstance().log("Failed to install " + packageName + "; shortError = " + shortError + "; fullError = " + fullError);
        }
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        for (EventObserver observer : mObservers)
            observer.onInstallationFailed(sessionId, shortError, fullError, exception);

    }

    @Nullable
    private String getRawError(Intent intent) {
        return intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
    }

    private String parseError(Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -999);
        String otherPackage = intent.getStringExtra(PackageInstaller.EXTRA_OTHER_PACKAGE_NAME);
        String error = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        int errorCode = intent.getIntExtra(ANDROID_PM_EXTRA_LEGACY_STATUS, AndroidPackageInstallerError.UNKNOWN.getLegacyErrorCode());

        if (status == STATUS_BAD_ROM) {
            return mContext.getString(R.string.installer_error_lidl_rom);
        }

        AndroidPackageInstallerError androidPackageInstallerError = getAndroidPmError(errorCode, error);
        if (androidPackageInstallerError != AndroidPackageInstallerError.UNKNOWN) {
            return androidPackageInstallerError.getDescription(mContext);
        }

        return getSimplifiedErrorDescription(status, otherPackage);
    }

    public String getSimplifiedErrorDescription(int status, String blockingPackage) {
        switch (status) {
            case PackageInstaller.STATUS_FAILURE_ABORTED:
                return mContext.getString(R.string.installer_error_aborted);

            case PackageInstaller.STATUS_FAILURE_BLOCKED:
                String blocker = mContext.getString(R.string.installer_error_blocked_device);
                if (blockingPackage != null) {
                    String appLabel = Utils.getAppLabel(mContext, blockingPackage);
                    if (appLabel != null)
                        blocker = appLabel;
                }
                return mContext.getString(R.string.installer_error_blocked, blocker);

            case PackageInstaller.STATUS_FAILURE_CONFLICT:
                return mContext.getString(R.string.installer_error_conflict);

            case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                return mContext.getString(R.string.installer_error_incompatible);

            case PackageInstaller.STATUS_FAILURE_INVALID:
                return mContext.getString(R.string.installer_error_bad_apks);

            case PackageInstaller.STATUS_FAILURE_STORAGE:
                return mContext.getString(R.string.installer_error_storage);

            case STATUS_BAD_ROM:
                return mContext.getString(R.string.installer_error_lidl_rom);
        }
        return mContext.getString(R.string.installer_error_generic);
    }

    public AndroidPackageInstallerError getAndroidPmError(int legacyErrorCode, @Nullable String error) {
        for (AndroidPackageInstallerError androidPackageInstallerError : AndroidPackageInstallerError.values()) {
            if (androidPackageInstallerError.getLegacyErrorCode() == legacyErrorCode || (error != null && error.startsWith(androidPackageInstallerError.getError())))
                return androidPackageInstallerError;
        }
        return AndroidPackageInstallerError.UNKNOWN;
    }

    public interface EventObserver {

        default void onConfirmationPending(int sessionId, @Nullable String packageName) {

        }

        default void onInstallationSucceeded(int sessionId, String packageName) {

        }

        default void onInstallationFailed(int sessionId, String shortError, @Nullable String fullError, @Nullable Exception exception) {

        }
    }

}
