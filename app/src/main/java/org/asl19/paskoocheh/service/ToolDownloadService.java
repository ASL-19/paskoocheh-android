package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.telephony.TelephonyManager;
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
import org.asl19.paskoocheh.data.AmazonToolRequest;
import org.asl19.paskoocheh.data.source.AmazonDataSource;
import org.asl19.paskoocheh.data.source.AmazonRepository;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.utils.Connectivity;
import org.parceler.Parcels;

import java.io.File;

import javax.inject.Inject;

/**
 * Service for retrieving version configuration file from
 * Amazon S3.
 */
public class ToolDownloadService extends IntentService {

    private static final String AMAZON = "s3";
    public static final String TEMP = ".temp";

    @Inject
    TransferUtility transferUtility;

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
        try {
            this.version = Parcels.unwrap(intent.getExtras().getParcelable("VERSION"));

            Integer urlSplit = version.s3Key.lastIndexOf("/");
            String directory = version.s3Key.substring(0, urlSplit);
            String externalFile = version.s3Key.substring(urlSplit + 1);

            final NotificationManager notificationManager
                    = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);


            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setOngoing(true);
            if (version.packageName != null &&
                    version.packageName.equals(this.getPackageName())) {
                notificationBuilder.setContentTitle(getString(R.string.update));
            } else {
                notificationBuilder.setContentTitle(version.appName)
                        .setContentText(getString(R.string.download_in_progress))
                        .setProgress(100, 0, false);
                notificationManager.notify(version.toolId, notificationBuilder.build());
            }

            final File internalFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk" + TEMP, version.getAppName(), version.getVersionNumber()));
            final TransferObserver observer = transferUtility.download(version.s3Bucket + directory, externalFile, internalFile);
            final long startTime = System.currentTimeMillis();

            sharedPreferences.edit().putInt(version.packageName, observer.getId()).commit();

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        long totalTime = System.currentTimeMillis() - startTime;
                        registerDownloadRequest(AmazonToolRequest.DOWNLOAD, totalTime);

                        sharedPreferences.edit().remove(version.packageName).commit();

                        notificationBuilder.setProgress(0, 0, true);
                        notificationBuilder.setContentText(getString(R.string.verifying));
                        notificationManager.notify(version.toolId, notificationBuilder.build());

                        Intent configIntent = new Intent(ToolDownloadService.this, ToolDownloadSecurityService.class);
                        configIntent.putExtras(intent);
                        startService(configIntent);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    notificationBuilder.setProgress((int) bytesTotal, (int) bytesCurrent, false);
                    notificationManager.notify(version.toolId, notificationBuilder.build());
                }

                @Override
                public void onError(int id, Exception ex) {
                    long totalTime = System.currentTimeMillis() - startTime;
                    registerDownloadRequest(AmazonToolRequest.FAILED, totalTime);

                    sharedPreferences.edit().remove(version.packageName).commit();
                    for (File file: new File(getApplicationContext().getFilesDir() + "/").listFiles()) {
                        if (file.getName().startsWith(version.appName)) {
                            file.delete();
                        }
                    }

                    Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
                    notificationIntent.putExtras(intent);
                    PendingIntent pendingIntent = PendingIntent.getService(
                            getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    notificationBuilder.setContentIntent(pendingIntent);
                    notificationBuilder.setOngoing(false);
                    notificationBuilder.setContentText(getString(R.string.download_failed_retry));
                    notificationBuilder.setProgress(0, 0, false);
                    notificationManager.notify(version.toolId, notificationBuilder.build());

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    getApplicationContext(),
                                    String.format(getString(R.string.retry_tool_download), version.appName),
                                    Toast.LENGTH_SHORT
                            ).show();                }
                    });

                    FirebaseCrashlytics.getInstance().recordException(ex);

                    Log.e("ToolDownloadService", ex.toString());
                }
            });
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            FirebaseCrashlytics.getInstance().log(version.appName + " " + version.s3Bucket + ""  + version.s3Key);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            String.format(getString(R.string.retry_tool_download), version.appName),
                            Toast.LENGTH_SHORT
                    ).show();                }
            });
        }
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
            public void onSubmitRequestSuccessful() {
            }

            @Override
            public void onSubmitRequestFailed() {
            }
        });
    }
}
