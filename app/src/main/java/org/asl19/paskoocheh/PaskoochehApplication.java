package org.asl19.paskoocheh;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.leakcanary.LeakCanary;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import ie.equalit.ouinet.Config;
import ie.equalit.ouinet.Ouinet;
import org.asl19.paskoocheh.amazon.AmazonComponenet;
import org.asl19.paskoocheh.amazon.DaggerAmazonComponenet;
import org.asl19.paskoocheh.service.ConfigJobCreator;

import java.util.Locale;

import lombok.Getter;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.OUINET_PREF;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PRIMARY_CHANNEL;

public class PaskoochehApplication extends Application {
    private static final String LOGTAG = "OuinetPaskoocheh";
    public static Ouinet mOuinet;
    public static boolean USE_SERVICE = true;
    public static Config ouinetConfig;
    public static String injectorCert;
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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        //Ouniet

        injectorCert = getResources().getString(R.string.ouinet_injector_tls_cert);

        ouinetConfig = new Config.ConfigBuilder(this)
                .setCacheHttpPubKey(getResources().getString(R.string.ouinet_cache_http_pubkey))
                .setInjectorCredentials(getResources().getString(R.string.ouinet_injector_credentials))
                .setInjectorTlsCert(injectorCert)
                .setTlsCaCertStorePath("cert/cacert.pem")
                .setCacheType(getResources().getString(R.string.ouinet_cache_type))
                .build();

        if (injectorCert != null) {
            Log.i(LOGTAG, "Injector's TLS certificate:");
            for (String line : injectorCert.split("\n")) {
                Log.i(LOGTAG, "\"" + line + "\"");
            }
        }
        //Log.d(LOGTAG,"ouinet"+String.valueOf(getApplicationContext().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(OUINET_PREF, true)));
        if (getApplicationContext().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(OUINET_PREF,false)){
            if (USE_SERVICE) {
                Log.d(LOGTAG, " --------- Starting ouinet service");
                OuinetService.startOuinetService(this, ouinetConfig);
            } else {
                Log.d(LOGTAG, " --------- Starting ouinet in activity");
                mOuinet = new Ouinet(this, ouinetConfig);
                mOuinet.start();
            }
        }

        LeakCanary.install(this);
        // Normal app init code...


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
//    @Override
//    protected void onDestroy() {
//        Log.d(LOGTAG, "---------------- Destroying----------------------------");
//        if (USE_SERVICE) {
//            Log.d(LOGTAG, " ---- Using service, not doing anything ---");
//        } else {
//            if (mOuinet != null) {
//                mOuinet.stop();
//                mOuinet = null;
//            }
//        }
        
}
