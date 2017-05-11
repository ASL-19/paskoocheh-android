package org.asl19.paskoocheh.baseactivities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.about.AboutActivity;
import org.asl19.paskoocheh.data.source.DownloadCountRepository;
import org.asl19.paskoocheh.data.source.ToolRepository;
import org.asl19.paskoocheh.installedtoollist.InstalledToolListActivity;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.terms.TermsActivity;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.asl19.paskoocheh.update.UpdateDialogFragment;
import org.asl19.paskoocheh.update.UpdateDialogPresenter;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.utils.FontStyle;
import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;
import static org.asl19.paskoocheh.Constants.UPDATE_NOTIFICATION;

public class BaseNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseNavigationContract.NavigationView{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    DynamoDBMapper dynamoDBMapper;

    private BaseNavigationContract.Presenter presenter;

    private FirebaseAnalytics firebaseAnalytics;

    AndroidTool paskoocheh;

    List<AndroidTool> tools;

    TextView updatesAvailable;

    CheckBox wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(FontStyle.valueOf(FontStyle.Small.name()).getResId(), true);
        setContentView(R.layout.activity_base_navigation);
        ButterKnife.bind(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ((PaskoochehApplication) getApplication()).getAmazonComponenet().inject(this);

        new BaseNavigationPresenter(this, new ToolRepository(getBaseContext(), getPackageManager()), new DownloadCountRepository());

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        initializeNavigationDrawer();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initializeNavigationDrawer() {
        ButterKnife.bind(navigationView.getMenu(), this);

        if (navigationView != null) {
            LinearLayout myApps
                    = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_installed_apps));
            updatesAvailable = (TextView) myApps.findViewById(R.id.updates_available);

            presenter.getInstalledTools();
            presenter.getAndroidTools();

            navigationView.setNavigationItemSelectedListener(this);

            MenuItem version = navigationView.getMenu().findItem(R.id.nav_version);
            version.setTitle(getString(R.string.version) + " " + BuildConfig.VERSION_NAME);

            LinearLayout downloadWifiLayout
                    = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_wifi));
            wifiSwitch = (CheckBox) downloadWifiLayout.findViewById(R.id.wifi_checkbox);

            wifiSwitch.setChecked(getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE)
                    .getBoolean(DOWNLOAD_WIFI, true));
            wifiSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit().putBoolean(
                            DOWNLOAD_WIFI,
                            wifiSwitch.isChecked()
                    ).commit();
                }
            });

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            drawerLayout.addDrawerListener(toggle);

            toggle.syncState();

            getSupportActionBar().show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (!item.isChecked()) {
            drawerLayout.closeDrawer(navigationView, false);
            Bundle bundle = new Bundle();
            switch (item.getItemId()) {
                case R.id.nav_apps:
                    startActivity(new Intent(this, ToolListActivity.class));
                    break;
                case R.id.nav_installed_apps:
                    startActivity(new Intent(this, InstalledToolListActivity.class));
                    break;
                case R.id.nav_send:
                    String location = "https://paskoocheh.com/";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, location);
                    startActivity(Intent.createChooser(share, getString(R.string.app_name)));
                    break;
                case R.id.nav_self_update:
                    if (paskoocheh != null) {
                        installTool(paskoocheh);
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.up_to_date), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                case R.id.nav_wifi:
                    wifiSwitch.setChecked(!wifiSwitch.isChecked());
                    getSharedPreferences(PASKOOCHEH_PREFS,
                            Context.MODE_PRIVATE).edit().putBoolean(DOWNLOAD_WIFI,
                            wifiSwitch.isChecked()
                    ).commit();

                    bundle.putString(Constants.SCREEN, BaseNavigationActivity.class.getName());
                    bundle.putBoolean(DOWNLOAD_WIFI, wifiSwitch.isChecked());
                    FirebaseAnalytics.getInstance(this).logEvent(Constants.DOWNLOAD_WIFI, bundle);
                    break;
                case R.id.nav_version:
                    break;
                case R.id.nav_about:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
                case R.id.nav_privacy:
                    startActivity(new Intent(this, TermsActivity.class));
                    break;
                case R.id.nav_feedback:
                    bundle.putString(Constants.SCREEN, BaseNavigationActivity.class.getName());
                    FirebaseAnalytics.getInstance(this).logEvent(Constants.FEEDBACK, bundle);
                    firebaseAnalytics.logEvent("feedback", bundle);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"bepors@asl19.org"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Paskoocheh Feedback");
                    intent.putExtra(Intent.EXTRA_TEXT, "Paskoocheh Version " + BuildConfig.VERSION_NAME);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.no_email, Toast.LENGTH_SHORT).show();
                    }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onPermissionsRequested(Integer code) {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean requestGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (requestGranted) {
            Toast.makeText(getApplicationContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();

            for (AndroidTool androidTool: tools) {
                if (androidTool.getToolId().intValue() == requestCode) {
                    installTool(androidTool);
                    return;
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.required_write), Toast.LENGTH_SHORT).show();
        }
    }

    private void installTool(AndroidTool tool) {
        File toolFile = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + tool.getName() + ".apk");
        String uuid = getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getString(PASKOOCHEH_UUID, "");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            onPermissionsRequested(tool.getToolId().intValue());
        } else if (toolFile.exists()) {
            ApkManager.installPackage(this, tool.getChecksum(), toolFile);
        } else {
            ConnectivityManager connManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (!getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                presenter.registerDownload(uuid, tool.getEnglishName(), dynamoDBMapper);
                Intent intent = new Intent(this, ToolDownloadService.class);
                intent.putExtra("TOOL", Parcels.wrap(tool));
                startService(intent);
                Toast.makeText(this, getString(R.string.queued), Toast.LENGTH_SHORT).show();
            } else if ((activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)) {
                Toast.makeText(this, getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean isActive() {
        return !isFinishing();
    }

    @Override
    public void setPresenter(BaseNavigationContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getInstalledToolsSuccessful(List<AndroidTool> tools) {
        int updatesAvailableTotal = 0;
        for (AndroidTool availableTool : tools) {
            if (availableTool.isUpdateAvailable()) {
                updatesAvailableTotal++;

                long notificationTime = getSharedPreferences(PASKOOCHEH_PREFS,
                        Context.MODE_PRIVATE).getLong(UPDATE_NOTIFICATION, -1);

                if (availableTool.getPackageName().equals(getPackageName())
                        && (notificationTime == -1 || DateUtils.DAY_IN_MILLIS < (System.currentTimeMillis() - notificationTime)))
                {
                    paskoocheh = availableTool;

                    getSharedPreferences(PASKOOCHEH_PREFS,
                            Context.MODE_PRIVATE).edit().putLong(UPDATE_NOTIFICATION, System.currentTimeMillis()).apply();

                    UpdateDialogFragment updateDialogFragment = UpdateDialogFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("PASKOOCHEH", Parcels.wrap(paskoocheh));
                    updateDialogFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    updateDialogFragment.show(fragmentManager, updateDialogFragment.getClass().getName());

                    new UpdateDialogPresenter(updateDialogFragment, new DownloadCountRepository());
                }
            }
        }

        updatesAvailable.setVisibility(View.VISIBLE);
        String updates = "";
        if (updatesAvailableTotal == 0) {
            updatesAvailable.setVisibility(View.INVISIBLE);
        } else if (updatesAvailableTotal == 1) {
            updates = "%s " + getString(R.string.update);
        } else {
            updates = "%s " + getString(R.string.updates);
        }
        updatesAvailable.setText(String.format(updates, Integer.toString(updatesAvailableTotal)));
    }

    @Override
    public void getInstalledToolsFailed() {
        updatesAvailable.setVisibility(View.INVISIBLE);
    }

    @Override
    public void getToolsSuccessful(List<AndroidTool> tools) {
        this.tools = tools;
    }

    @Override
    public void getToolsFailed() {

    }

    @Override
    public void onRegisterDownloadSuccessful() {

    }

    @Override
    public void onRegisterDownloadFailed() {

    }
}
