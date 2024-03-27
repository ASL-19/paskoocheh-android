package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.aefyr.sai.installer2.impl.rootless.RootlessSaiPiBroadcastReceiver;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.AmazonToolRequest;
import org.asl19.paskoocheh.data.source.AmazonDataSource;
import org.asl19.paskoocheh.data.source.AmazonRepository;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.utils.Connectivity;
import org.asl19.paskoocheh.amazon.S3Clients;
import org.parceler.Parcels;

import java.io.File;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.BUCKET_NAME;

/**
 * Service for retrieving version configuration file from
 * Amazon S3.
 */
public class ToolDownloadService extends IntentService {

    private static final String TAG = "ToolDownloadService";

    private static final String AMAZON = "s3";
    public static final String TEMP = ".temp";

    public static final String INTENT_EXTRA_P2P_DOWNLOAD_FAILED = "intent_extra_p2p_download_failed";

    @Inject
    S3Clients s3Clients;

    private Version version;

    private SharedPreferences sharedPreferences;

    /**
     * Paskoocheh Config Service.
     */
    public ToolDownloadService() {
        super("PaskoochehConfigService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((PaskoochehApplication) getApplication()).getAmazonComponenet().inject(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
            public void afterResponse(Request<?> request, Response<?> response) {}

            @Override
            public void afterError(Request<?> request, Response<?> response, Exception e) {}
        };

        final NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setOngoing(true);

        try {
            this.version = Parcels.unwrap(intent.getExtras().getParcelable(InstallFragment.VERSION));

            Integer urlSplit = version.getS3Key().lastIndexOf("/");
            String directory = version.getS3Key().substring(0, urlSplit);
            String externalFile = version.getS3Key().substring(urlSplit + 1);
            String tmp[] = externalFile.split("\\.");
            String fileExtension = tmp[tmp.length - 1];

            AmazonS3Client s3Client = s3Clients.chooseClient();

            s3Client.addRequestHandler(requestHandler);
            TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();

            if (version.getPackageName() != null &&
                    version.getPackageName().equals(this.getPackageName())) {
                notificationBuilder.setContentTitle(getString(R.string.update));
            } else {
                notificationBuilder.setContentTitle(version.getAppName())
                        .setContentText(getString(R.string.download_in_progress))
                        .setProgress(100, 0, false);
            }

            startForeground(version.getToolId(), notificationBuilder.build());

            final File internalFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s." + fileExtension + TEMP, version.getAppName(), version.getVersionNumber()));
            final TransferObserver observer = transferUtility.download(version.getS3Bucket() + directory, externalFile, internalFile);
            final long startTime = System.currentTimeMillis();
            sharedPreferences.edit().putInt(version.getPackageName(), observer.getId()).commit();

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        Log.d(TAG, "download file completed");
                        long totalTime = System.currentTimeMillis() - startTime;
                        registerDownloadRequest(AmazonToolRequest.DOWNLOAD, totalTime);

                        sharedPreferences.edit().remove(version.getPackageName()).commit();

                        stopForeground(true);
                        Intent configIntent = new Intent(ToolDownloadService.this, ToolDownloadSecurityService.class);
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

                    // When the request goes through a proxy, the
                    // Content-Length HTTP header field may be dropped (e.g.
                    // when the proxy sets Transfer-Encoding: chunked). If that
                    // happens, let's use the size as specified in the
                    // `version` object.
                    int total = bytesTotal != 0 ? (int) bytesTotal : version.size;
                    notificationBuilder.setProgress(total, (int) bytesCurrent, false);
                    notificationManager.notify(version.getToolId(), notificationBuilder.build());
                }

                @Override
                public void onError(int id, Exception ex) {
                    long totalTime = System.currentTimeMillis() - startTime;
                    registerDownloadRequest(AmazonToolRequest.FAILED, totalTime);
                    Log.d(TAG, "download file error " + ex.getStackTrace().toString());
                    sharedPreferences.edit().remove(version.getPackageName()).commit();
                    for (File file: new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                        if (file.getName().startsWith(version.getAppName())) {
                            file.delete();
                        }
                    }

                    //toast(getApplicationContext(), getString(R.string.retry_tool_download), version.appName);

                    FirebaseCrashlytics.getInstance().recordException(ex);
                    sendNetworkErrorIntent(getApplicationContext());

                    Log.e(TAG, ex.toString());
                    showErrorSystemTrayNotification(intent, notificationManager, notificationBuilder);
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, "download file error 2 " + ex.getStackTrace().toString());
            FirebaseCrashlytics.getInstance().recordException(ex);
            FirebaseCrashlytics.getInstance().log(version.getAppName() + " " + version.getS3Bucket() + ""  + version.getS3Key());
            //toast(getApplicationContext(), getString(R.string.retry_tool_download), version.getAppName());
            sendNetworkErrorIntent(getApplicationContext());
            showErrorSystemTrayNotification(intent, notificationManager, notificationBuilder);
        }
    }

    private void showErrorSystemTrayNotification(Intent intent, NotificationManager notificationManager, NotificationCompat.Builder notificationBuilder) {
        Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
        notificationIntent.putExtras(intent);
        PendingIntent pendingIntent = PendingIntent.getService(
                getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setOngoing(false);
        notificationBuilder.setContentText(getString(R.string.download_failed_retry));
        notificationBuilder.setProgress(0, 0, false);
        notificationManager.notify(version.getToolId(), notificationBuilder.build());
        stopForeground(false);
    }

     static void  sendNetworkErrorIntent(Context context) {
         Log.d(TAG, "sendNetworkErrorIntent");
        Intent intent = new Intent();

        intent.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);
        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_IS_INSTALL_SUCCESS, false);
        intent.putExtra(INTENT_EXTRA_P2P_DOWNLOAD_FAILED, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void toast(Context context, String message, String appName) {
        Log.d(TAG, "Toast: " + message + " (" + appName + ")");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        context,
                        String.format(message, appName),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void registerDownloadRequest(String type, long time) {

        AmazonToolRequest amazonToolRequest = new AmazonToolRequest(getApplicationContext());
        amazonToolRequest.setType(type);
        amazonToolRequest.setTool(version.getAppName());
        amazonToolRequest.setToolVersion(version.getVersionNumber());
        amazonToolRequest.setFileSize(version.getSize().toString());
        amazonToolRequest.setDownloadTime(String.valueOf(time));
        amazonToolRequest.setDownloadedVia(AMAZON);

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            amazonToolRequest.setNetworkName(telephonyManager.getNetworkOperatorName());
            amazonToolRequest.setNetworkCountry(telephonyManager.getNetworkCountryIso());
        }

        NetworkInfo networkInfo = Connectivity.getNetworkInfo(getApplicationContext());
        if (networkInfo != null) {
            amazonToolRequest.setNetworkType(Connectivity.getConnectionType(networkInfo.getType(), networkInfo.getSubtype()));
        }

        new AmazonRepository(getApplicationContext()).onSubmitRequest(amazonToolRequest, new AmazonDataSource.SubmitRequestCallback() {
            @Override
            public void onSubmitRequestSuccessful() { }

            @Override
            public void onSubmitRequestFailed() { }
        });
    }
    public String pathComponent(String pathResource) {
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
        // Add Version Code
        if (this.version != null) {
            pathResource += ";version_code=" + this.version.versionCode.toString();
        }
        return pathResource;
    }
}
