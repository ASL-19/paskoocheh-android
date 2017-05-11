package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.receiver.AmazonS3StartReceiver;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.CONFIG_DIRECTORY;
import static org.asl19.paskoocheh.Constants.CONFIG_FILE;

/**
 * Service for retrieving tool configuration file from
 * Amazon S3.
 */
public class PaskoochehConfigService extends IntentService {

    @Inject
    TransferUtility transferUtility;

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
        final File file = new File(getApplicationContext().getFilesDir() + "/" + CONFIG_FILE);
        final TransferObserver observer = transferUtility.download(BUCKET_NAME + CONFIG_DIRECTORY, CONFIG_FILE, file);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    AmazonS3StartReceiver.completeWakefulIntent(intent);
                    EventBus.getDefault().post(new Event.PaskoochehConfigComplete());
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("PaskoochehConfigService", ex.toString());
            }
        });
    }
}
