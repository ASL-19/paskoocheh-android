package org.asl19.paskoocheh;


import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import org.asl19.paskoocheh.amazon.AmazonComponenet;
import org.asl19.paskoocheh.amazon.DaggerAmazonComponenet;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import lombok.Getter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PaskoochehApplication extends Application {

    @Getter
    private AmazonComponenet amazonComponenet;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        amazonComponenet = DaggerAmazonComponenet.builder()
                .paskoochehApplicationModule(new PaskoochehApplicationModule(getApplicationContext()))
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

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
