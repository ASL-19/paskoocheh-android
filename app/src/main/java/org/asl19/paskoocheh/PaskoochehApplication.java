package org.asl19.paskoocheh;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.evernote.android.job.JobManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.leakcanary.LeakCanary;

import org.asl19.paskoocheh.amazon.AmazonComponenet;
import org.asl19.paskoocheh.amazon.DaggerAmazonComponenet;
import org.asl19.paskoocheh.service.ConfigJobCreator;

import java.util.Locale;

import lombok.Getter;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        amazonComponenet = DaggerAmazonComponenet.builder()
                .paskoochehApplicationModule(new PaskoochehApplicationModule(getApplicationContext()))
                .build();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/IRANSans.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/IRANSans.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL, getString(R.string.app_name), IMPORTANCE_LOW);
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
