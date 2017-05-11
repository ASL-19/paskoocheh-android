package org.asl19.paskoocheh.loading;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.receiver.AmazonS3StartReceiver;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;

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

        // Periodic PaskoochehConfigDownload service
        sendBroadcast(new Intent(this, AmazonS3StartReceiver.class));
        startAmazonS3Service();
    }

    @Subscribe
    public void paskoochehConfigDownloadComplete(Event.PaskoochehConfigComplete paskoochehConfigComplete) {
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
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void generateUserId() {
        SharedPreferences settings = getSharedPreferences(PASKOOCHEH_PREFS, 0);

        if (!settings.contains(PASKOOCHEH_UUID)) {
            settings.edit().putString(PASKOOCHEH_UUID, UUID.randomUUID().toString()).commit();
        }
    }

    private void startAmazonS3Service() {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AmazonS3StartReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        );
    }
}