package org.asl19.paskoocheh.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aefyr.sai.viewmodels.InstallerXDialogViewModel;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.Version;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InstallAABService extends IntentService {
    private static final String TAG = "InstallAABService";

    private InstallerXDialogViewModel mViewModel;
    private Version version;

    public InstallAABService() {
        super("InstallAABService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mViewModel = new InstallerXDialogViewModel(this, null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            Log.d(TAG, "onHandleIntent");
            this.version = Parcels.unwrap(intent.getExtras().getParcelable(InstallFragment.VERSION));
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.PRIMARY_CHANNEL);
            notificationBuilder.setOngoing(true);

            notificationBuilder.setContentTitle(version.getAppName());
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setProgress(0, 0, true);
            notificationBuilder.setContentText(getString(R.string.install_notification_state_installing));

            startForeground(version.getToolId(), notificationBuilder.build());

            File internalFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.zip", version.getAppName(), version.getVersionNumber()));
            List<File> filesToInstall = new ArrayList<>();
            filesToInstall.add(internalFile);
            mViewModel.setApkSourceFiles(filesToInstall);
        }
    }
}