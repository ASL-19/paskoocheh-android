package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.source.LastModifiedDataSource;
import org.asl19.paskoocheh.data.source.Local.LastModifiedLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.pojo.LastModified;
import org.asl19.paskoocheh.utils.AppExecutors;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.CONFIG_DIRECTORY;

/**
 * Service for retrieving tool configuration file from
 * Amazon S3.
 */
public class PaskoochehConfigService extends IntentService {

    public static final String CONFIG = "CONFIG";

    @Inject
    TransferUtility transferUtility;

    @Inject
    AmazonS3Client amazonS3Client;

    private Long amazonLastModifiedTime;
    /**
     * Paskoocheh Config Service.
     */
    public PaskoochehConfigService() {
        super("PaskoochehConfigService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((PaskoochehApplication) getApplication()).getAmazonComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        try {
            final String configFile = intent.getExtras().getString(CONFIG);

            amazonLastModifiedTime = amazonS3Client.getObjectMetadata(BUCKET_NAME + CONFIG_DIRECTORY, configFile).getLastModified().getTime();

            PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
            LastModifiedDataSource lastModifiedDataSource = LastModifiedLocalDataSource.getInstance(new AppExecutors(), database.lastModifiedDao());
            lastModifiedDataSource.getLastModified(configFile, new LastModifiedDataSource.GetLastModifiedCallback() {
                @Override
                public void onGetLastModifiedSuccessful(LastModified lastModified) {
                    if (lastModified.getLastModified() < amazonLastModifiedTime) {
                        downloadFile(intent, configFile);
                    } else {
                        if (configFile.equals(APPS)) {
                            EventBus.getDefault().post(new Event.PaskoochehConfigComplete());
                        }
                    }
                }

                @Override
                public void onGetLastModifiedFailed() {
                    downloadFile(intent, configFile);
                }
            });
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new Event.Timeout());
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void downloadFile(final Intent intent, final String configFile) {
        final File file = new File(getApplicationContext().getFilesDir() + "/" + configFile);
        final TransferObserver observer = transferUtility.download(BUCKET_NAME + CONFIG_DIRECTORY, configFile, file);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Intent configIntent = new Intent(PaskoochehConfigService.this, PaskoochehConfigSecurityService.class);
                    configIntent.putExtras(intent);
                    startService(configIntent);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                EventBus.getDefault().post(new Event.Timeout());

                Toast.makeText(
                        PaskoochehConfigService.this,
                        getString(R.string.download_failed_retry),
                        Toast.LENGTH_SHORT
                ).show();

                FirebaseCrashlytics.getInstance().recordException(ex);

                Log.e("PaskoochehConfigService", ex.toString());
            }
        });
    }
}
