package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Version;
import org.parceler.Parcels;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.ASC;

/**
 * Service for retrieving version configuration file from
 * Amazon S3.
 */
public class ToolDownloadSecurityService extends IntentService {

    @Inject
    TransferUtility transferUtility;

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
            this.version = Parcels.unwrap(intent.getExtras().getParcelable("VERSION"));

            Integer urlSplit = version.getS3Key().lastIndexOf("/");
            String directory = version.getS3Key().substring(0, urlSplit);
            String externalFile = version.getS3Key().substring(urlSplit + 1) + ASC;

            final NotificationManager notificationManager
                    = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);

            final File internalSecurityFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk" + ASC, version.getAppName(), version.getVersionNumber()));

            final TransferObserver observer = transferUtility.download(version.getS3Bucket() + directory, externalFile, internalSecurityFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        Intent configIntent = new Intent(ToolDownloadSecurityService.this, ToolDownloadVerificationService.class);
                        configIntent.putExtras(intent);
                        startService(configIntent);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    for (File file : new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                        if (file.getName().startsWith(version.getAppName())) {
                            file.delete();
                        }
                    }

                    Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
                    notificationIntent.putExtra("VERSION", Parcels.wrap(version));
                    PendingIntent pendingIntent = PendingIntent.getService(
                            getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    notificationBuilder.setContentTitle(version.getAppName());
                    notificationBuilder.setSmallIcon(R.drawable.ic_notification);
                    notificationBuilder.setContentIntent(pendingIntent);
                    notificationBuilder.setOngoing(false);
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
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    Log.e(this.getClass().getSimpleName(), ex.toString());
                }
            });
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            FirebaseCrashlytics.getInstance().log(version.appName + " " + version.s3Bucket + " " + version.s3Key);


        }
    }
}
