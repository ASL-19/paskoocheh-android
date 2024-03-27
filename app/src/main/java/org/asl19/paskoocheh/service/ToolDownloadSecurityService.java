package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.os.Build;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.Version;
import org.parceler.Parcels;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.ASC;
import org.asl19.paskoocheh.amazon.S3Clients;

/**
 * Service for retrieving version configuration file from
 * Amazon S3.
 */
public class ToolDownloadSecurityService extends IntentService {
    private static final String TAG = "ToolDownloadSecurityService";

    @Inject
    S3Clients s3Clients;

    private Version version;

    public ToolDownloadSecurityService() {
        super("ToolDownloadSecurityService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        ((PaskoochehApplication) getApplication()).getAmazonComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(@NonNull final Intent intent) {
        try {
            this.version = Parcels.unwrap(intent.getExtras().getParcelable(InstallFragment.VERSION));

            Integer urlSplit = version.getS3Key().lastIndexOf("/");
            String directory = version.getS3Key().substring(0, urlSplit);
            String externalFile = version.getS3Key().substring(urlSplit + 1);
            String[] tmp = externalFile.split("\\.");
            String fileExtension = tmp[tmp.length-1];
            externalFile = externalFile + ASC;

            final NotificationManager notificationManager
                    = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);

            notificationBuilder.setContentTitle(version.getAppName());
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setOngoing(true);
            notificationBuilder.setProgress(0, 0, true);
            notificationBuilder.setContentText(getString(R.string.verifying));
            startForeground(version.getToolId(), notificationBuilder.build());

            final File internalSecurityFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension + ASC, version.getAppName(), version.getVersionNumber()));

            final TransferObserver observer = s3Clients.chooseTransferUtility().download(version.getS3Bucket() + directory, externalFile, internalSecurityFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(TAG, "state = " + state.toString());
                    if (state == TransferState.COMPLETED) {
                        Log.d(TAG, "downloading security file completed");
                        stopForeground(true);
                        Intent configIntent = new Intent(ToolDownloadSecurityService.this, ToolDownloadVerificationService.class);
                        configIntent.putExtras(intent);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(configIntent);
                        } else {
                            startService(configIntent);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(TAG, "security file download error = " + ex.getStackTrace().toString());
                    for (File file : new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                        if (file.getName().startsWith(version.getAppName())) {
                            file.delete();
                        }
                    }

                    Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
                    notificationIntent.putExtra("VERSION", Parcels.wrap(version));
                    PendingIntent pendingIntent = PendingIntent.getService(
                            getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //ToolDownloadService.toast(getApplicationContext(), getString(R.string.retry_tool_download), version.getAppName());
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    ToolDownloadService.sendNetworkErrorIntent(getApplicationContext());
                    Log.e(this.getClass().getSimpleName(), ex.toString());

                    stopForeground(true);
                    notificationBuilder.setContentIntent(pendingIntent);
                    notificationBuilder.setOngoing(false);
                    notificationBuilder.setContentText(getString(R.string.download_failed_retry));
                    notificationBuilder.setProgress(0, 0, false);
                    notificationManager.notify(version.getToolId(), notificationBuilder.build());
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, "security file download error 2 = " + ex.getStackTrace().toString());
            FirebaseCrashlytics.getInstance().recordException(ex);
            FirebaseCrashlytics.getInstance().log(version.appName + " " + version.s3Bucket + " " + version.s3Key);
            ToolDownloadService.sendNetworkErrorIntent(getApplicationContext());
        }
    }
}
