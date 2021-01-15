package org.asl19.paskoocheh;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.squareup.leakcanary.LeakCanary;

import org.asl19.paskoocheh.amazon.AmazonComponenet;
import org.asl19.paskoocheh.amazon.DaggerAmazonComponenet;
import org.asl19.paskoocheh.service.ConfigJobCreator;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import lombok.Getter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;
import static org.asl19.paskoocheh.Constants.PRIMARY_CHANNEL;

public class PaskoochehApplication extends Application {

    @Getter
    private AmazonComponenet amazonComponenet;

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new ConfigJobCreator(getApplicationContext()));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        Fabric.with(this, new Crashlytics());

        amazonComponenet = DaggerAmazonComponenet.builder()
                .paskoochehApplicationModule(new PaskoochehApplicationModule(getApplicationContext()))
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL, getString(R.string.app_name), IMPORTANCE_LOW);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        setLocale();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }

    private void setLocale() {
        Locale locale = new Locale("fa");

        Configuration conf = getBaseContext().getResources().getConfiguration();
        updateConfiguration(conf, locale);
        getBaseContext().getResources().updateConfiguration(conf, getResources().getDisplayMetrics());

        Configuration systemConf = Resources.getSystem().getConfiguration();
        updateConfiguration(systemConf, locale);
        Resources.getSystem().updateConfiguration(conf, getResources().getDisplayMetrics());

        Locale.setDefault(locale);
    }

    void updateConfiguration(Configuration conf, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(locale);
        } else {
            conf.locale = locale;
        }
    }
}
