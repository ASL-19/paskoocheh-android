package org.asl19.paskoocheh;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.leakcanary.LeakCanary;
import android.content.Intent;

import ie.equalit.ouinet.Config;
import org.asl19.paskoocheh.amazon.AmazonComponenet;
import org.asl19.paskoocheh.amazon.DaggerAmazonComponenet;
import org.asl19.paskoocheh.p2pnetwork.P2PAlerts;
import org.asl19.paskoocheh.service.ConfigJobCreator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Locale;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDexApplication;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import lombok.Getter;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;

import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.CONFIG_DIRECTORY;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.OUINET_DIR;
import static org.asl19.paskoocheh.Constants.OUINET_PREF;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PRIMARY_CHANNEL;

import org.asl19.paskoocheh.amazon.S3Clients;

import javax.inject.Inject;

public class PaskoochehApplication extends MultiDexApplication {
    public enum OuinetServiceState {
        STARTED, STOPPED
    }

    @Inject
    S3Clients s3Clients;

    public static final String OUINET_DIR_NAME = ".ouinet";

    private static final String LOGTAG = "OuinetPaskoocheh";

    public static OuinetServiceState ouinetServiceState = OuinetServiceState.STOPPED;
    public static String injectorCert;

    // Each time the ouinetConfig changes, we increment the
    // ouinetConfigVersion. We then send this version to the OuinetService
    // together with the config. If the OuinetService receives a config version
    // higher than whatever it has seen so far, it will restart the mOuinet
    // instance with the new config.
    private int ouinetConfigVersion = 0;
    private Config.ConfigBuilder ouinetConfigBuilder;

    public SSLContext sslContext;
    public UnifiedTrustManager trustManager;

    private static PaskoochehApplication appInstance = null;

    public static PaskoochehApplication getInstance() {
        return appInstance;
    }

    private P2PAlerts mP2PAlerts;

    @Getter
    private AmazonComponenet amazonComponenet;

    // To see whether this service is running, you may try this command:
    // adb -s $mi shell dumpsys activity services OuinetService
    public void startOuinetService() {
        Intent intent = new Intent(this, OuinetService.class);
        intent.putExtra(OuinetService.CONFIG_VERSION_EXTRA, ouinetConfigVersion);
        intent.putExtra(OuinetService.CONFIG_EXTRA, ouinetConfigBuilder.build());
        try {
            startService(intent);
            ouinetServiceState = OuinetServiceState.STARTED;
            addPreferenceOuinetService();
        } catch (IllegalStateException e) {
            // This happens on some more recent Android OS version due to this:
            // https://stackoverflow.com/q/46445265/273348
            // Let's ignore it, and then try starting the service again once the
            // app comes to foreground.
            Log.d(LOGTAG, "Failed to start Ouinet service (app in background?)");
            Intent intentStartFail = new Intent(OuinetService.ACTION_OUINET_STATUS_BROADCAST);
            intent.putExtra(OuinetService.EXTRA_IS_OUINET_START_SUCCESSFUL, false);
            sendBroadcast(intentStartFail);
        }
    }

    public void stopOuinetService() {
        if (ouinetServiceState == OuinetServiceState.STOPPED) return;
        Intent intent = new Intent(this, OuinetService.class);
        intent.putExtra(OuinetService.EXTRA_STOP_SERVICE_COMMAND, true);
        startService(intent);
        ouinetServiceState = OuinetServiceState.STOPPED;
        addPreferenceOuinetService();
    }

    public void addPreferenceOuinetService()
    {
        this.getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit().putBoolean(
                OUINET_PREF,
                isOuinetStarted()
        ).commit();
    }

    public static boolean isOuinetStarted() {
        return PaskoochehApplication.ouinetServiceState
            == PaskoochehApplication.OuinetServiceState.STARTED;
    }

    public boolean setOuinetCacheStaticContentPath(String dir)
    {
        Config oldConfig = ouinetConfigBuilder.build();

        String oldDir = oldConfig.getCacheStaticContentPath();

        if (oldDir != null && oldDir.equals(dir)) {
            return false;
        }

        File file = new File(dir);

        if (!file.exists()) {
            return false;
        }

        if (!file.isDirectory()) {
            return false;
        }

        if (!new File(dir + "/" + OUINET_DIR_NAME).isDirectory()) {
            return false;
        }

        ouinetConfigBuilder.setCacheStaticContentPath(dir);
        ouinetConfigBuilder.setCacheStaticPath(dir + "/" + OUINET_DIR_NAME);

        ouinetConfigVersion += 1;

        startOuinetService();

        return true;
    }

    /**
     * Get an OkHttp client builder with the app's trusted certificates,
     * the given interceptors, and logging.
     */
    public OkHttpClient.Builder getOkHttpClientBuilder(Interceptor... interceptors) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        for (Interceptor int_ : interceptors)
            httpClient.addInterceptor(int_);

        httpClient.sslSocketFactory(sslContext.getSocketFactory(), trustManager);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        if (BuildConfig.BUILD_TYPE.equals("dev")) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }


        if (isOuinetStarted()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                     new InetSocketAddress(OuinetService.PROXY_HOST, OuinetService.PROXY_PORT));

            httpClient.proxy(proxy);
        }

        return httpClient.addInterceptor(loggingInterceptor);
    }

    private void toast(String message) {
        Log.d(LOGTAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        if (appInstance != null) {
            throw new IllegalArgumentException("Assumption was that this is called once per process");
        }

        appInstance = this;

        super.onCreate();

        mP2PAlerts = new P2PAlerts(this);


        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        JobManager.create(this).addJobCreator(new ConfigJobCreator(getApplicationContext()));
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        createOuinetWithDirectory();

        if (userEnabledOuinetService()) {
            startOuinetService();
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

    // ------------------Ouinet config adding directory to test--------------------

    public P2PAlerts getP2PAlerts() {
        return this.mP2PAlerts;
    }

    public void createOuinetWithDirectory() {
        String directoryPathSelected = getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE)
                .getString(OUINET_DIR,"Download");
        injectorCert = getResources().getString(R.string.ouinet_injector_tls_cert);

        ouinetConfigBuilder = new Config.ConfigBuilder(this)
            .setCacheHttpPubKey(getResources().getString(R.string.ouinet_cache_http_pubkey))
            .setInjectorCredentials(getResources().getString(R.string.ouinet_injector_credentials))
            .setInjectorTlsCert(injectorCert)
            .setTlsCaCertStorePath("file:///android_asset/cert/cacert.pem")
            .setCacheType(getResources().getString(R.string.ouinet_cache_type))
            .setCachePrivate(true)
            .setDisableOriginAccess(true);

        if(!directoryPathSelected.equals("Download")) {
            ouinetConfigBuilder
                .setCacheStaticPath(directoryPathSelected + "/" + OUINET_DIR_NAME)
                .setCacheStaticContentPath(directoryPathSelected);
        }

        // These are only for debugging, enable as needed.
//        ouinetConfigBuilder
//            .setDisableOriginAccess(true)
//            .setDisableProxyAccess(true)
//            .setDisableInjectorAccess(true)
//            .setLogLevel(Config.LogLevel.DEBUG);

        Config ouinetConfig = ouinetConfigBuilder.build();

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
    }
    public void setCustomCertificateAuthority(InputStream inputStream) {

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
            trustManager = new UnifiedTrustManager(keyStore);

            // Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            SSLContext.setDefault(sslContext);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

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
    //Getting the OUINET_PREF
    public boolean userEnabledOuinetService() {
       return getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(OUINET_PREF, false);
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

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method that verifies whether the permissions of a given array are granted or not.
     *
     * @param context
     * @param permissions
     * @return {Boolean}
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isPortOpen(final String ip, final int port, final int timeout) {

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        }

        catch(ConnectException ce){
            ce.printStackTrace();
            return false;
        }

        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
