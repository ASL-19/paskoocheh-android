package org.asl19.paskoocheh.installreceiver;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.data.AmazonToolRequest;
import org.asl19.paskoocheh.data.source.AmazonDataSource;
import org.asl19.paskoocheh.data.source.AmazonRepository;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.utils.AppExecutors;
import org.asl19.paskoocheh.utils.Connectivity;

import java.io.File;
import java.util.List;

public class InstallListener extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        try{
            final String packageName = intent.getData().getSchemeSpecificPart();

            PaskoochehDatabase database = PaskoochehDatabase.getInstance(context.getApplicationContext());
            VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(new AppExecutors(), database.versionDao(), context.getApplicationContext());
            versionLocalDataSource.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
                @Override
                public void onGetVersionsSuccessful(List<Version> versions) {
                    try {
                        for (Version version : versions) {
                            if (version.getPackageName() != null &&
                                    version.getPackageName().equals(packageName)) {
                                for (File file : new File(context.getFilesDir() + "/").listFiles()) {
                                    if (file.getName().startsWith(version.getAppName())) {
                                        file.delete();
                                    }
                                }

                                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.cancel(version.getToolId().intValue());

                                NetworkInfo networkInfo = Connectivity.getNetworkInfo(context.getApplicationContext());
                                TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

                                AmazonToolRequest amazonToolRequest = new AmazonToolRequest(context);
                                amazonToolRequest.setType(AmazonToolRequest.INSTALL);
                                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                                    amazonToolRequest.setType(AmazonToolRequest.UPDATE);
                                }
                                amazonToolRequest.setTool(version.getAppName());
                                amazonToolRequest.setToolVersion(version.getVersionNumber());
                                amazonToolRequest.setFileSize(version.getSize().toString());

                                amazonToolRequest.setNetworkName(telephonyManager.getNetworkOperatorName());
                                amazonToolRequest.setNetworkCountry(telephonyManager.getNetworkCountryIso());
                                amazonToolRequest.setNetworkType(Connectivity.getConnectionType(networkInfo.getType(), networkInfo.getSubtype()));

                                new AmazonRepository(context).onSubmitRequest(amazonToolRequest, new AmazonDataSource.SubmitRequestCallback() {
                                    @Override
                                    public void onSubmitRequestSuccessful() {
                                    }

                                    @Override
                                    public void onSubmitRequestFailed() {
                                    }
                                });
                            }
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                    }
                }

                @Override
                public void onGetVersionsFailed() {
                }
            });
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }
}