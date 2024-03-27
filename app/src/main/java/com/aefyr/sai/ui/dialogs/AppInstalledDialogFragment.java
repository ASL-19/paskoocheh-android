package com.aefyr.sai.ui.dialogs;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.R;

import java.util.Objects;

public class AppInstalledDialogFragment extends DialogFragment {
    private static final String ARG_PACKAGE = "package";
    private static final String ARG_APP_NAME = "app_name";
    private static final String ARG_ERROR_MESSAGE = "error_message";

    private String mPackage, mErrorMessage, mAppName;

    public static AppInstalledDialogFragment newInstance(String pkg, String appName, String errorMessage) {
        AppInstalledDialogFragment fragment = new AppInstalledDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PACKAGE, pkg);
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        args.putString(ARG_APP_NAME, appName);
        fragment.setArguments(args);
        fragment.setCancelable(false);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null)
            return;

        mPackage = args.getString(ARG_PACKAGE, null);
        mAppName = args.getString(ARG_APP_NAME, null);
        mErrorMessage =  args.getString(ARG_ERROR_MESSAGE, null);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String appLabel = null;
        Intent appLaunchIntent = null;

        try {
            if (mErrorMessage == null) { // i.e. successfully installed the App.
                PackageManager pm = getContext().getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(mPackage, 0);
                appLaunchIntent = pm.getLaunchIntentForPackage(mPackage);
                Objects.requireNonNull(appLaunchIntent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                appLabel = pm.getApplicationLabel(appInfo).toString();
            } else {
                // if the installation failed then use AppName.
                appLabel = mAppName;
            }
        } catch (Exception e) {
            Log.w("SAI", e);
            appLabel = mAppName;
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setTitle(appLabel)
                .setMessage(mErrorMessage == null ? getString(R.string.installer_app_installed) : mErrorMessage)
                .setNegativeButton(R.string.ok, null);

        Intent finalAppLaunchIntent = appLaunchIntent;
        if (appLaunchIntent != null && mErrorMessage == null)
            builder.setPositiveButton(R.string.installer_open, (d, w) -> {
                try {
                    startActivity(finalAppLaunchIntent);
                } catch (ActivityNotFoundException e) {
                    Log.w("AppInstalledDialog", "Unable to launch activity", e);
                   // SimpleAlertDialogFragment.newInstance(getString(R.string.error), getString(R.string.installer_unable_to_launch_app)).show(getParentFragmentManager(), null);
                    // jay - TODO error dialog
                }
                dismiss();
            });

        return builder.create();
    }
}
