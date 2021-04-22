package org.asl19.paskoocheh.toollist;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;

import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseNavigationActivity;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.Local.DownloadAndRatingLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.ImagesLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.LocalizedInfoLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.NameLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.ToolLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static org.asl19.paskoocheh.PaskoochehApplication.ouinetConfig;
import static org.asl19.paskoocheh.PaskoochehApplication.sslSocketFactory;
import static org.asl19.paskoocheh.PaskoochehApplication.trustManager;

public class ToolListActivity extends BaseNavigationActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));
        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.apps));
        ToolListFragment toolListFragment =
                (ToolListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (toolListFragment == null) {
            toolListFragment = ToolListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), toolListFragment, R.id.contentFrame);
        }
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
        AppExecutors appExecutors = new AppExecutors();
        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        DownloadAndRatingLocalDataSource downloadAndRatingLocalDataSource = DownloadAndRatingLocalDataSource.getInstance(appExecutors, database.downloadAndRatingDao());
        ImagesDataSource imagesDataSource = ImagesLocalDataSource.getInstance(appExecutors, database.imagesDao());
        LocalizedInfoDataSource localizedInfoDataSource = LocalizedInfoLocalDataSource.getInstance(appExecutors, database.localizedInfoDao());
        NameDataSource nameDataSource = NameLocalDataSource.getInstance(appExecutors, database.nameDao());
        ToolDataSource toolDataSource = ToolLocalDataSource.getInstance(appExecutors, database.toolDao());
        new ToolListPresenter(toolListFragment, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource, nameDataSource, toolDataSource);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_apps).setChecked(true);
    }
}
