package org.asl19.paskoocheh.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toollist.ToolListFragment;
import org.asl19.paskoocheh.amazon.S3Clients;

import java.io.File;

import javax.inject.Inject;

/**
 * This class handles the installation and uninstallation
 * of applications.
 */
public class ApkManager {

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    @Inject
    S3Clients s3Clients;

    private Context context;

    /**
     * Create an ApkManager
     **/
    public ApkManager(Context context) {
        this.context = context;
        ((PaskoochehApplication) context.getApplicationContext()).getAmazonComponenet().inject(this);
    }

    /**
     * Install the given application file iff the given checksum matches or is empty.
     *
     * @param version application
     * @param file A apk for which to install.
     * @return true iff the given apk was installed.
     */
    public void installPackage(Version version, File file) {
        if (!checkIsDownloading(version.getPackageName())) {
            if (version.getChecksum().isEmpty() || Checksum.checkChecksum(version.getChecksum(), file)) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
                bundle.putString(Constants.TOOL_ID, file.getName());
                FirebaseAnalytics.getInstance(context).logEvent(Constants.INSTALL, bundle);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                file.setReadable(true, false);
                Uri internalUri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 24) {
                    internalUri = FileProvider.getUriForFile(context, AUTHORITY, file);
                }

                intent.setDataAndType(internalUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, R.string.checksum_invalid, Toast.LENGTH_LONG).show();
                for (File internalFile: new File(context.getFilesDir() + "/").listFiles()) {
                    if (internalFile.getName().startsWith(version.getAppName())) {
                        internalFile.delete();
                    }
                }
            }
        } else {
            Toast.makeText(context, R.string.download_in_progress, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Uninstall the application with the given package name.
     *
     * @param packageName Package name of application to be uninstalled.
     */
    public void uninstallPackage(String packageName) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
        bundle.putString(Constants.TOOL_ID, packageName);
        FirebaseAnalytics.getInstance(context).logEvent(Constants.UNINSTALL, bundle);

        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private boolean checkIsDownloading(String packageName) {
        final TransferUtility transferUtility = s3Clients.chooseTransferUtility();
        int id = PreferenceManager.getDefaultSharedPreferences(context).getInt(packageName, 0);
        TransferObserver transferObserver = transferUtility.getTransferById(id);
        if (transferObserver != null) {
            if (transferObserver.getState().equals(TransferState.IN_PROGRESS)) {
                return true;
            } else {
                transferUtility.cancel(id);
            }
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(packageName).apply();
        return false;
    }
}
