package org.asl19.paskoocheh;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.http.X509TrustManagerExtensions;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.leakcanary.LeakCanary;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import ie.equalit.ouinet.Config;
import ie.equalit.ouinet.Ouinet;
import org.asl19.paskoocheh.amazon.AmazonComponenet;
import org.asl19.paskoocheh.amazon.DaggerAmazonComponenet;
import org.asl19.paskoocheh.service.ConfigJobCreator;
import org.asl19.paskoocheh.toollist.LoggingInterceptor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import lombok.Getter;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import okhttp3.OkHttpClient;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.OUINET_PREF;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PRIMARY_CHANNEL;

public class PaskoochehApplication extends Application{

    private static final String LOGTAG = "OuinetPaskoocheh";
    public static Ouinet mOuinet;
    public static boolean USE_SERVICE = true;
    public static Config ouinetConfig;
    public static String injectorCert;
    public static SSLSocketFactory sslSocketFactory;
    public static UnifiedTrustManager trustManager;
    public static OkHttpClient client;
    @Getter
    private AmazonComponenet amazonComponenet;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        JobManager.create(this).addJobCreator(new ConfigJobCreator(getApplicationContext()));
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        //Ouniet

        injectorCert = getResources().getString(R.string.ouinet_injector_tls_cert);

        ouinetConfig = new Config.ConfigBuilder(this)
                .setCacheHttpPubKey(getResources().getString(R.string.ouinet_cache_http_pubkey))
                .setInjectorCredentials(getResources().getString(R.string.ouinet_injector_credentials))
                .setInjectorTlsCert(injectorCert)
                .setTlsCaCertStorePath("file:///android_asset/cert/cacert.pem")
                .setCacheType(getResources().getString(R.string.ouinet_cache_type))
                .build();
            Log.i(LOGTAG, "TLS Store Path's:"+ouinetConfig.getInjectorTlsCertPath());
        try {
            File initialFile = new File(ouinetConfig.getCaRootCertPath());
            InputStream ca = new FileInputStream(initialFile);
            Log.i(LOGTAG, "Ca Root Store Path's:"+ouinetConfig.getCaRootCertPath());
            setCustomCertificateAuthority(ca);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (injectorCert != null) {
            Log.i(LOGTAG, "Injector's TLS certificate:");
            for (String line : injectorCert.split("\n")) {
                Log.i(LOGTAG, "\"" + line + "\"");
            }
        }

        if (getApplicationContext().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(OUINET_PREF, false)) {
            if (USE_SERVICE) {
                Log.d(LOGTAG, " --------- Starting ouinet service");
                OuinetService.startOuinetService(this, ouinetConfig);
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


    public static void setCustomCertificateAuthority(InputStream inputStream) {

        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(inputStream);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN() + Arrays.toString(((X509Certificate) ca).getSignature()));
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore and system CA
            UnifiedTrustManager trustManager = new UnifiedTrustManager(keyStore);

                // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{trustManager}, null);

            SSLContext.setDefault(context);
            // Tell the URLConnection to use a SocketFactory from our SSLContext
            client = new OkHttpClient.Builder().sslSocketFactory(context.getSocketFactory(),trustManager)
                 .addInterceptor(new LoggingInterceptor())
                     .build();
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());




        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }

    private static class UnifiedTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public UnifiedTrustManager(KeyStore localKeyStore) throws KeyStoreException {
            try {
                this.defaultTrustManager = createTrustManager(null);
                this.localTrustManager = createTrustManager(localKeyStore);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        private X509TrustManager createTrustManager(KeyStore store) throws NoSuchAlgorithmException, KeyStoreException {
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(store);

            if (store == null) {
                for (TrustManager tm : tmf.getTrustManagers()) {
                    if (tm instanceof X509TrustManager) {
                        return (X509TrustManager) tm;
                    }
                }
            }

            TrustManager[] trustManagers = tmf.getTrustManagers();
            return (X509TrustManager) trustManagers[0];
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkClientTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkClientTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] first = defaultTrustManager.getAcceptedIssuers();
            Log.i("DEFAULT", first.toString());
            X509Certificate[] second = localTrustManager.getAcceptedIssuers();
            Log.i("LOCAL", second.toString());
            X509Certificate[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
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
//    public void onDestroy() {
//        Log.d(LOGTAG, "---------------- Destroying----------------------------");
//        if (USE_SERVICE) {
//            Log.d(LOGTAG, " ---- Using service, not doing anything ---");
//        } else {
//            if (mOuinet != null) {
//                mOuinet.stop();
//                mOuinet = null;
//            }
//        }
//
//    }
}