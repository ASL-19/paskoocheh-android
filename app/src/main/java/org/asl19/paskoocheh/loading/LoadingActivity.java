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
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.asl19.paskoocheh.utils.PaskoochehContextWrapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.Constants.DOWNLOADS_AND_RATINGS;
import static org.asl19.paskoocheh.Constants.FAQS;
import static org.asl19.paskoocheh.Constants.GUIDES_AND_TUTORIALS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;
import static org.asl19.paskoocheh.Constants.REVIEWS;
import static org.asl19.paskoocheh.Constants.TEXTS;
import static org.asl19.paskoocheh.service.PaskoochehConfigService.CONFIG;

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

        Intent configIntent = new Intent(this, PaskoochehConfigService.class);
        configIntent.putExtra(CONFIG, APPS);
        startService(configIntent);


        // Periodic PaskoochehConfigDownload service
        startAmazonS3Service();
    }

    @Subscribe
    public void paskoochehConfigDownloadComplete(Event.PaskoochehConfigComplete paskoochehConfigComplete) {

        Intent configIntent = new Intent(this, PaskoochehConfigService.class);
        configIntent.putExtra(CONFIG, DOWNLOADS_AND_RATINGS);
        startService(configIntent);
        configIntent.putExtra(CONFIG, FAQS);
        startService(configIntent);
        configIntent.putExtra(CONFIG, GUIDES_AND_TUTORIALS);
        startService(configIntent);
        configIntent.putExtra(CONFIG, REVIEWS);
        startService(configIntent);
        configIntent.putExtra(CONFIG, TEXTS);
        startService(configIntent);

        Intent intent = new Intent(this, ToolListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        super.attachBaseContext(CalligraphyContextWrapper.wrap(PaskoochehContextWrapper.wrap(newBase)));
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