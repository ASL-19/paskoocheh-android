package org.asl19.paskoocheh.loading;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.service.ConfigJobCreator;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.asl19.paskoocheh.utils.PaskoochehContextWrapper;
import org.asl19.paskoocheh.p2ploading.LoadingP2PActivity;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;

import java.util.UUID;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoadingActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        EventBus.getDefault().register(this);

        LoadingFragment loadingFragment =
                (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (loadingFragment == null) {
            loadingFragment = LoadingFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), loadingFragment, R.id.contentFrame);
        }

        generateUserId();

        PaskoochehConfigService.startLoadingAppsConfig(this);

        // Periodic PaskoochehConfigDownload service
        startAmazonS3Service();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppsConfigDownloadComplete(Event.AppsConfigComplete e) {
        PaskoochehConfigService.startLoadingOtherConfigs(this);
        ToolListActivity.start(this);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void paskoochehConfigTimeout(Event.Timeout timeout) {
        Intent intent = new Intent(this, LoadingP2PActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(PaskoochehContextWrapper.wrap(newBase)));
    }

    private void generateUserId() {
        SharedPreferences settings = getSharedPreferences(PASKOOCHEH_PREFS, 0);

        if (!settings.contains(PASKOOCHEH_UUID)) {
            settings.edit().putString(PASKOOCHEH_UUID, UUID.randomUUID().toString()).commit();
        }
    }

    private void startAmazonS3Service() {
        ConfigJobCreator.scheduleJob();
    }
}
