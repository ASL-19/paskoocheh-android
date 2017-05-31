package org.asl19.paskoocheh.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.crashlytics.android.Crashlytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.utils.Checksum;
import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static org.asl19.paskoocheh.Constants.BUCKET_NAME;

/**
 * Service for retrieving tool configuration file from
 * Amazon S3.
 */
public class ToolDownloadService extends IntentService {

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    @Inject
    TransferUtility transferUtility;

    private AndroidTool tool;

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
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        this.tool = Parcels.unwrap(intent.getExtras().getParcelable("TOOL"));

        final NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setOngoing(true);
        if (tool.getPackageName().equals(this.getPackageName())) {
            notificationBuilder.setContentTitle(getString(R.string.update));
        } else {
            notificationBuilder.setContentTitle(tool.getName())
                    .setContentText(getString(R.string.download_in_progress))
                    .setProgress(100, 0, false);
            notificationManager.notify(tool.getToolId().intValue(), notificationBuilder.build());
        }

        File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        final File internalFile = new File(path, tool.getName() + ".apk");

        Integer urlSplit = tool.getDownloadUrl().lastIndexOf("/");
        String directory = tool.getDownloadUrl().substring(0, urlSplit);
        String externalFile = tool.getDownloadUrl().substring(urlSplit + 1);

        final TransferObserver observer = transferUtility.download(BUCKET_NAME + directory, externalFile, internalFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    
                    try {
                        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE))
                                .addCompletedDownload(
                                        tool.getName(), tool.getDescription(),
                                        true, "application/vnd.android.package-archive",
                                        internalFile.getPath(),
                                        internalFile.length(), false);
                    } catch (IllegalArgumentException ex) {
                        Crashlytics.logException(ex);
                    }

                    notificationBuilder.setProgress(0, 0, false);
                    notificationManager.notify(tool.getToolId().intValue(), notificationBuilder.build());

                    if (tool.getChecksum().isEmpty() || Checksum.checkChecksum(tool.getChecksum(), internalFile)) {

                        Uri internalUri = Uri.fromFile(internalFile);
                        if (Build.VERSION.SDK_INT >= 24) {
                            internalUri = FileProvider.getUriForFile(getApplicationContext(), AUTHORITY, internalFile);
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(internalUri, "application/vnd.android.package-archive");

                        List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            getApplicationContext().grantUriPermission(packageName, internalUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        notificationBuilder.setOngoing(false);
                        notificationBuilder.setContentIntent(pendingIntent);
                        notificationBuilder.setProgress(0, 0, false);
                        notificationManager.notify(tool.getToolId().intValue(), notificationBuilder.build());
                        ApkManager.installPackage(getApplicationContext(), tool.getChecksum(), internalFile);
                    } else {
                        notificationManager.cancel(tool.getToolId().intValue());
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                notificationBuilder.setProgress((int) bytesTotal, (int) bytesCurrent, false);
                notificationManager.notify(tool.getToolId().intValue(), notificationBuilder.build());
            }

            @Override
            public void onError(int id, Exception ex) {
                internalFile.delete();

                Intent notificationIntent = new Intent(getApplicationContext(), ToolDownloadService.class);
                notificationIntent.putExtra("TOOL", Parcels.wrap(tool));
                PendingIntent pendingIntent = PendingIntent.getService(
                        getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                notificationBuilder.setContentIntent(pendingIntent);
                notificationBuilder.setOngoing(false);
                notificationBuilder.setContentText(getString(R.string.download_failed_retry));
                notificationBuilder.setProgress(0, 0, false);
                notificationManager.notify(tool.getToolId().intValue(), notificationBuilder.build());

                Toast.makeText(
                        ToolDownloadService.this,
                        String.format(getString(R.string.retry_tool_download), tool.getName()),
                        Toast.LENGTH_SHORT
                ).show();

                Crashlytics.logException(ex);
                Log.e("ToolDownloadService", ex.toString());
            }
        });
    }
}
