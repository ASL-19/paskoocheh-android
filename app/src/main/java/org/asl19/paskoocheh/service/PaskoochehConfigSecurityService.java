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
import org.asl19.paskoocheh.event.Event;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.ASC;
import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.CONFIG_DIRECTORY;

/**
 * Service for retrieving tool configuration file from
 * Amazon S3.
 */
public class PaskoochehConfigSecurityService extends IntentService {

    public static final String CONFIG = "CONFIG";

    @Inject
    TransferUtility transferUtility;

    @Inject
    AmazonS3Client amazonS3Client;

    private String originalFilename;

    private String securityFilename;

    /**
     * Paskoocheh Config Service.
     */
    public PaskoochehConfigSecurityService() {
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
            originalFilename = intent.getExtras().getString(CONFIG);
            securityFilename = originalFilename + ASC;
            downloadFile(intent);
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

    private void downloadFile(final Intent intent) {
        final File securityFile = new File(getApplicationContext().getFilesDir() + "/" + securityFilename);
        final TransferObserver observer = transferUtility.download(BUCKET_NAME + CONFIG_DIRECTORY, securityFilename, securityFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Intent configIntent = new Intent(PaskoochehConfigSecurityService.this, PaskoochehConfigVerificationService.class);
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
                        PaskoochehConfigSecurityService.this,
                        getString(R.string.download_failed_retry),
                        Toast.LENGTH_SHORT
                ).show();

                FirebaseCrashlytics.getInstance().recordException(ex);

                Log.e("SecurityConfigService", ex.toString());
            }
        });
    }
}
