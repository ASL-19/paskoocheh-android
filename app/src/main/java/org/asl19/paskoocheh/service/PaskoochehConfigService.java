package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.RequestHandler2;
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
import org.asl19.paskoocheh.amazon.S3Clients;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.CONFIG_DIRECTORY;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.Constants.DOWNLOADS_AND_RATINGS;
import static org.asl19.paskoocheh.Constants.FAQS;
import static org.asl19.paskoocheh.Constants.GUIDES_AND_TUTORIALS;
import static org.asl19.paskoocheh.Constants.REVIEWS;
import static org.asl19.paskoocheh.Constants.TEXTS;

/**
 * Service for retrieving tool configuration file from
 * Amazon S3.
 */
public class PaskoochehConfigService extends IntentService {

    public static final String CONFIG = "CONFIG";

    @Inject
    S3Clients s3Clients;

    private Long amazonLastModifiedTime;
    /**
     * Paskoocheh Config Service.
     */
    public PaskoochehConfigService() {
        super("PaskoochehConfigService");
    }

    static public void startLoadingAppsConfig(Context ctx) {
        Intent intent = new Intent(ctx, PaskoochehConfigService.class);
        intent.putExtra(CONFIG, APPS);
        ctx.startService(intent);
    }

    static public void startLoadingOtherConfigs(Context ctx) {
        Intent configIntent = new Intent(ctx, PaskoochehConfigService.class);
        configIntent.putExtra(CONFIG, DOWNLOADS_AND_RATINGS);
        ctx.startService(configIntent);
        configIntent.putExtra(CONFIG, FAQS);
        ctx.startService(configIntent);
        configIntent.putExtra(CONFIG, GUIDES_AND_TUTORIALS);
        ctx.startService(configIntent);
        configIntent.putExtra(CONFIG, REVIEWS);
        ctx.startService(configIntent);
        configIntent.putExtra(CONFIG, TEXTS);
        ctx.startService(configIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((PaskoochehApplication) getApplication()).getAmazonComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        RequestHandler2 requestHandler = new RequestHandler2() {
            @Override
            public void beforeRequest(Request<?> request) {
                String resourcePath = pathComponent(request.getResourcePath());
                request.addHeader("X-Ouinet-Group", resourcePath);
            }

            @Override
            public void afterResponse(Request<?> request, Response<?> response) {


            }

            @Override
            public void afterError(Request<?> request, Response<?> response, Exception e) {

            }
        };
        try {
            final String configFile = intent.getExtras().getString(CONFIG);
            final AmazonS3Client s3Client = s3Clients.chooseClient();

            s3Client.addRequestHandler(requestHandler);
            amazonLastModifiedTime = s3Client.getObjectMetadata(BUCKET_NAME + CONFIG_DIRECTORY, configFile).getLastModified().getTime();
            PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
            LastModifiedDataSource lastModifiedDataSource = LastModifiedLocalDataSource.getInstance(new AppExecutors(), database.lastModifiedDao());
            lastModifiedDataSource.getLastModified(configFile, new LastModifiedDataSource.GetLastModifiedCallback() {
                @Override
                public void onGetLastModifiedSuccessful(LastModified lastModified) {
                    if (lastModified.getLastModified() != amazonLastModifiedTime) {
                        downloadFile(intent, configFile);
                    } else {
                        if (configFile.equals(APPS)) {
                            EventBus.getDefault().post(new Event.AppsConfigComplete());
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
            EventBus.getDefault().post(new Event.Timeout());
        }
    }

    private void downloadFile(final Intent intent, final String configFile) {
        final File file = new File(getApplicationContext().getFilesDir() + "/" + configFile);
        final TransferUtility transferUtility = s3Clients.chooseTransferUtility();
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

    public static String pathComponent(String pathResource) {
        /*
        Check if pathResource starts with BUCKET_NAME(paskoocheh-repo or paskoocheh-dev|staging-storage),
        if not concatenate with BUCKET_NAME
        */
        if (!pathResource.startsWith(BUCKET_NAME)) {
            pathResource = BUCKET_NAME + "/" + pathResource;
        }
        /*Remove the filename from the path
         */
        int pos = pathResource.lastIndexOf('/');
        if (pos > -1) {
            pathResource = pathResource.substring(0, pos);
        }

        return pathResource;
    }
}
