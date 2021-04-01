package org.asl19.paskoocheh.service;


import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.utils.Checksum;
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

    private Version version;

    private ApkManager apkManager;



    public ToolDownloadVerificationService() {
        super("ToolDownloadVerificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        apkManager = new ApkManager(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setOngoing(false);

        try {
            this.version = Parcels.unwrap(intent.getExtras().getParcelable("VERSION"));
            notificationBuilder.setContentTitle(version.getAppName());

            final File internalTempFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk" + TEMP, version.getAppName(), version.getVersionNumber()));
            final File internalSecurityFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk" + ASC, version.getAppName(), version.getVersionNumber()));

            if ((version.getChecksum().isEmpty() || Checksum.checkChecksum(version.getChecksum(), internalTempFile))
                    && verifySignature(
                    new BufferedInputStream(new FileInputStream(internalTempFile)),
                    new BufferedInputStream(new FileInputStream(internalSecurityFile)),
                    new BufferedInputStream(getApplicationContext().getAssets().open("public_key.pub"))
            )) {

                File internalFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk", version.getAppName(), version.getVersionNumber()));
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

                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0, installIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                notificationBuilder.setContentIntent(pendingIntent);
                notificationBuilder.setProgress(0, 0, false);
                notificationManager.notify(version.getToolId(), notificationBuilder.build());

                apkManager.installPackage(version, internalFile);
            } else {
                notificationManager.cancel(version.getToolId());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                getApplicationContext(),
                                String.format(getString(R.string.checksum_invalid), version.getAppName()),
                                Toast.LENGTH_SHORT
                        ).show(); }
                });

                for (File file : new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                    if (file.getName().startsWith(version.getAppName())) {
                        file.delete();
                    }
                }
            }
        } catch (Exception exception) {
            for (File file: new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                if (file.getName().startsWith(version.getAppName())) {
                    file.delete();
                }
            }

            Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
            notificationIntent.putExtra("VERSION", Parcels.wrap(version));
            PendingIntent pendingIntent = PendingIntent.getService(
                    getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setContentText(getString(R.string.download_failed_retry));
            notificationBuilder.setProgress(0, 0, false);
            notificationManager.notify(version.getToolId(), notificationBuilder.build());

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            String.format(getString(R.string.retry_tool_download), version.getAppName()),
                            Toast.LENGTH_SHORT
                    ).show();                }
            });

            FirebaseCrashlytics.getInstance().recordException(exception);
            Log.e(this.getClass().getSimpleName(), exception.toString());
        }
    }
}
