package org.asl19.paskoocheh.baseactivities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.about.AboutActivity;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.feedback.FeedbackActivity;
import org.asl19.paskoocheh.installedtoollist.InstalledToolListActivity;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.terms.TermsActivity;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.asl19.paskoocheh.update.UpdateDialogFragment;
import org.asl19.paskoocheh.update.UpdateDialogPresenter;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.utils.AppExecutors;
import org.asl19.paskoocheh.utils.FontStyle;
import org.asl19.paskoocheh.utils.PaskoochehContextWrapper;
import org.parceler.Parcels;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.SCREEN;
import static org.asl19.paskoocheh.Constants.SHARE;
import static org.asl19.paskoocheh.Constants.UPDATE_NOTIFICATION;

public class BaseNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseNavigationContract.NavigationView{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private BaseNavigationContract.Presenter presenter;

    private FirebaseAnalytics firebaseAnalytics;

    Version paskoocheh;

    List<Version> versions = new ArrayList<>();

    TextView updatesAvailable;

    CheckBox wifiSwitch;

    private ApkManager apkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(FontStyle.valueOf(FontStyle.Small.name()).getResId(), true);
        setContentView(R.layout.activity_base_navigation);
        ButterKnife.bind(this);

        apkManager = new ApkManager(getApplicationContext());

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(new AppExecutors(), database.versionDao(), getApplicationContext());
        new BaseNavigationPresenter(this, versionLocalDataSource);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        initializeNavigationDrawer();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(PaskoochehContextWrapper.wrap(newBase)));

    }

    private void initializeNavigationDrawer() {
        ButterKnife.bind(navigationView.getMenu(), this);

        if (navigationView != null) {
            LinearLayout myApps
                    = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_installed_apps));
            updatesAvailable = (TextView) myApps.findViewById(R.id.updates_available);

            presenter.getInstalledVersions();
            presenter.getAndroidVersions();

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
                    try {
                        if (paskoocheh != null) {
                            int installedVersionCode = getPackageManager().getPackageInfo(paskoocheh.getPackageName(), 0).versionCode;
                            if (paskoocheh.getVersionCode() > installedVersionCode) {
                                paskoocheh.setUpdateAvailable(true);
                                installTool(paskoocheh);
                                return false;
                            }
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {}
                    Toast.makeText(getBaseContext(), getString(R.string.up_to_date), Toast.LENGTH_SHORT).show();
                    return false;
                case R.id.nav_wifi:
                    wifiSwitch.setChecked(!wifiSwitch.isChecked());
                    getSharedPreferences(PASKOOCHEH_PREFS,
                            Context.MODE_PRIVATE).edit().putBoolean(DOWNLOAD_WIFI,
                            wifiSwitch.isChecked()
                    ).commit();

                    bundle.putString(SCREEN, BaseNavigationActivity.class.getName());
                    bundle.putBoolean(DOWNLOAD_WIFI, wifiSwitch.isChecked());
                    FirebaseAnalytics.getInstance(this).logEvent(DOWNLOAD_WIFI, bundle);
                    return false;
                case R.id.nav_telegram:
                    bundle.putString(SHARE, "telegram");
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
                    bundle.putString(SCREEN, BaseNavigationActivity.class.getName());
                    Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(getString(R.string.support_telegram)));
                    startActivity(telegram);
                    break;
                case R.id.nav_version:
                    return false;
                case R.id.nav_about:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
                case R.id.nav_privacy:
                    startActivity(new Intent(this, TermsActivity.class));
                    break;
                case R.id.nav_feedback:
                    startActivity(new Intent(this, FeedbackActivity.class));
            }
        }
        drawerLayout.closeDrawer(navigationView, false);
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

    private void installTool(Version version) {
        File toolFile = new File(getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk", version.getAppName(), version.getVersionNumber()));
        if (toolFile.exists()) {
            apkManager.installPackage(version, toolFile);
        } else {
            ConnectivityManager connManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (!getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                Intent intent = new Intent(this, ToolDownloadService.class);
                intent.putExtra("VERSION", Parcels.wrap(version));
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
    public void getInstalledVersionsSuccessful(List<Version> versions) {
        int updatesAvailableTotal = 0;
        for (Version availableVersion : versions) {
            if (availableVersion.isUpdateAvailable()) {
                updatesAvailableTotal++;

                long notificationTime = getSharedPreferences(PASKOOCHEH_PREFS,
                        Context.MODE_PRIVATE).getLong(UPDATE_NOTIFICATION, -1);

                if (availableVersion.getPackageName() != null &&
                        availableVersion.getPackageName().equals(getPackageName())) {
                    paskoocheh = availableVersion;
                    if (notificationTime == -1 || DateUtils.DAY_IN_MILLIS < (System.currentTimeMillis() - notificationTime)) {
                        getSharedPreferences(PASKOOCHEH_PREFS,
                                Context.MODE_PRIVATE).edit().putLong(UPDATE_NOTIFICATION, System.currentTimeMillis()).apply();

                        UpdateDialogFragment updateDialogFragment = UpdateDialogFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("PASKOOCHEH", Parcels.wrap(paskoocheh));
                        updateDialogFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        updateDialogFragment.show(fragmentManager, updateDialogFragment.getClass().getName());

                        new UpdateDialogPresenter(updateDialogFragment);
                    }
                }
            }
        }

        String updates = "";
        if (updatesAvailableTotal == 0) {
            updatesAvailable.setVisibility(View.INVISIBLE);
        } else if (updatesAvailableTotal == 1) {
            updatesAvailable.setVisibility(View.VISIBLE);
            updates = "%s " + getString(R.string.update);
        } else {
            updatesAvailable.setVisibility(View.VISIBLE);
            updates = "%s " + getString(R.string.updates);
        }
        updatesAvailable.setText(String.format(updates, Integer.toString(updatesAvailableTotal)));
    }

    @Override
    public void getInstalledVersionsFailed() {
        updatesAvailable.setVisibility(View.INVISIBLE);
    }

    @Override
    public void getVersionsSuccessful(List<Version> versions) {
        this.versions = versions;
    }

    @Override
    public void getVersionsFailed() {
    }
}
