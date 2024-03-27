package org.asl19.paskoocheh.baseactivities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.OuinetService;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.about.AboutActivity;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.feedback.FeedbackActivity;
import org.asl19.paskoocheh.installedtoollist.InstalledToolListActivity;
import org.asl19.paskoocheh.p2pnetwork.P2PActivity;
import org.asl19.paskoocheh.p2pnetwork.P2PAlerts;
import org.asl19.paskoocheh.p2pnetwork.P2PBaseFragment;
import org.asl19.paskoocheh.pojo.AppDownloadInfoForVersionCode;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.OUINET_PREF;
import static org.asl19.paskoocheh.Constants.SCREEN;
import static org.asl19.paskoocheh.Constants.SHARE;
import static org.asl19.paskoocheh.Constants.UPDATE_NOTIFICATION;

public class BaseNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseNavigationContract.NavigationView{
    private static final String LOGTAG = "BaseNavigationActivity";

    private static final String EXTRA_IS_COMMUNICATION_WITH_OUINET_INITIATED = "is_communication_with_ouinet_initiated";
    private static final String EXTRA_IS_OUINET_STOP_REQUEST_INITIATED = "is_ouinet_stop_request_initiated";
    private static final String EXTRA_IS_SAVED_INSTANCE_STATE_CALLED = "is_saved_instance_state_called";
    private static final String EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL = "pending_ouinet_status_operation_successful";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private BaseNavigationContract.Presenter presenter;

    private FirebaseAnalytics firebaseAnalytics;

    private Version paskoocheh;

    private List<Version> versions = new ArrayList<>();

    private TextView updatesAvailable;

    private SwitchCompat mP2PSwitch;

    private CheckBox wifiSwitch;

    private ApkManager apkManager;
    private boolean alertAccepted;

    private OuinetStatusReceiver mOuinetStatusReceiver;
    private P2PAlerts mP2PAlerts;

    private boolean isCommunicationWithOuinetInitiated;
    private boolean isOuinetStopRequestInitiated;

    private Boolean pendingOuinetStatusOperationSuccessful = null;
    private boolean isSavedInstanceStateCalled = false;

    protected void onNavigationDrawerOpenListener() {
        // Empty method. Let the Subclass activities override this method if they want to listen to this event.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mP2PAlerts = PaskoochehApplication.getInstance().getP2PAlerts();

        if (savedInstanceState != null) {
            isCommunicationWithOuinetInitiated = savedInstanceState.getBoolean(EXTRA_IS_COMMUNICATION_WITH_OUINET_INITIATED, false);
            isOuinetStopRequestInitiated = savedInstanceState.getBoolean(EXTRA_IS_OUINET_STOP_REQUEST_INITIATED, false);
            isSavedInstanceStateCalled = savedInstanceState.getBoolean(EXTRA_IS_SAVED_INSTANCE_STATE_CALLED, false);
            if (savedInstanceState.containsKey(EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL)) {
                pendingOuinetStatusOperationSuccessful = savedInstanceState.getBoolean(EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL);
            }
        }

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

        mOuinetStatusReceiver = new OuinetStatusReceiver();
        registerReceiver(mOuinetStatusReceiver, new IntentFilter(OuinetService.ACTION_OUINET_STATUS_BROADCAST));

        initializeNavigationDrawer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        isSavedInstanceStateCalled = true;
        outState.putBoolean(EXTRA_IS_COMMUNICATION_WITH_OUINET_INITIATED, isCommunicationWithOuinetInitiated);
        outState.putBoolean(EXTRA_IS_OUINET_STOP_REQUEST_INITIATED, isOuinetStopRequestInitiated);
        outState.putBoolean(EXTRA_IS_SAVED_INSTANCE_STATE_CALLED, true);
        if (pendingOuinetStatusOperationSuccessful != null) {
            outState.putBoolean(EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL, pendingOuinetStatusOperationSuccessful);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(PaskoochehContextWrapper.wrap(newBase)));
    }

    private void setOuinetPref(boolean isP2PEnabled) {
        getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit().putBoolean(OUINET_PREF, isP2PEnabled).commit();
    }

    private void initializeNavigationDrawer() {
        ButterKnife.bind(navigationView.getMenu(), this);

        if (navigationView != null) {
            LinearLayout myApps
                    = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_installed_apps));
            updatesAvailable = (TextView) myApps.findViewById(R.id.updates_available);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                navigationView.getLayoutParams().width =
                        (int) ((Resources.getSystem().getDisplayMetrics().widthPixels) * 0.50);
            } else {
                navigationView.getLayoutParams().width =
                        (int) ((Resources.getSystem().getDisplayMetrics().widthPixels) * 0.75);
            }
            navigationView.requestLayout();

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

            LinearLayout p2pActiveLayout
                    = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_p2p));
            mP2PSwitch = p2pActiveLayout.findViewById(R.id.p2pEnableButton);
            mP2PSwitch.setOnClickListener((v) -> {
                if (mP2PSwitch.isChecked()) {
                    if (PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
                        return;
                    }
                    alertAccepted = getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE)
                            .getBoolean(P2PBaseFragment.USER_ACCEPTED_OUINET_ALERT_PREF, false);
                    if (!alertAccepted) {
                        simpleAlert(this);
                    } else {
                        startOuinetServiceAndShowAlert();
                    }
                } else {
                    if (!PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
                        return;
                    }
                    setOuinetPref(false);
                    isCommunicationWithOuinetInitiated = true;
                    isOuinetStopRequestInitiated = true;
                    mP2PAlerts.showP2PDisconnectingAlert(getSupportFragmentManager());
                    PaskoochehApplication.getInstance().stopOuinetService();
                }
            });

            navigationView.getMenu().findItem(R.id.nav_p2p).setOnMenuItemClickListener(item -> {
                mP2PSwitch.performClick();
                return true;
            });

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    // Do whatever you want here
                }
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    onNavigationDrawerOpenListener();
                }
            };

            drawerLayout.addDrawerListener(toggle);

            toggle.syncState();

            getSupportActionBar().show();
        }
    }

    private void startOuinetServiceAndShowAlert() {
        isCommunicationWithOuinetInitiated = true;
        setOuinetPref(true);
        mP2PAlerts.showP2PConnectingAlert(getSupportFragmentManager());
        PaskoochehApplication.getInstance().startOuinetService();
    }

    //Adding the alert to verify the understand of P2P network seeding.
    public void simpleAlert(FragmentActivity view) {
        final Dialog dialog = new Dialog(view);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        Button positive_btn = dialog.findViewById(R.id.positive_btn);
        Button negative_btn = dialog.findViewById(R.id.negative_btn);

        positive_btn.setOnClickListener((v) -> {
            getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit()
                    .putBoolean(P2PBaseFragment.USER_ACCEPTED_OUINET_ALERT_PREF, true).commit();
            alertAccepted = true;
            dialog.dismiss();
            startOuinetServiceAndShowAlert();
        });

        negative_btn.setVisibility(View.GONE);

        dialog.setOnDismissListener(dialog1 -> {
            if (!alertAccepted) {
                mP2PSwitch.setChecked(false);
            }
        });

        dialog.show();
    }

    public class OuinetStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean startSuccess =
                    intent.getBooleanExtra(OuinetService.EXTRA_IS_OUINET_START_SUCCESSFUL, false);
            boolean stopComplete =
                    intent.getBooleanExtra(OuinetService.EXTRA_IS_OUINET_STOP_COMPLETE, false);
            Log.d(LOGTAG, "OuinetStatusReceiver.onReceive: startSuccess = " + startSuccess + "; stopComplete = " + stopComplete);

            onOuinetStatusUpdate(startSuccess || stopComplete);
        }
    }

    private void onOuinetStatusUpdate(boolean operationSuccessful) {

        if(isSavedInstanceStateCalled) {
            // The App is in background while this method was called by the OuinetStatusReceiver.onReceive.
            Log.v(LOGTAG, "onOuinetStatusUpdate isSavedInstanceStateCalled = true; operationSuccessful = " + operationSuccessful);
            pendingOuinetStatusOperationSuccessful = operationSuccessful;
            return;
        }
        pendingOuinetStatusOperationSuccessful = null;
        Log.v(LOGTAG, "onOuinetStatusUpdate operationSuccessful = " + operationSuccessful);
        if (isCommunicationWithOuinetInitiated) {
            P2PBaseFragment.alertsOnP2POperationCompletion(this, getSupportFragmentManager(), mP2PSwitch.isChecked(), mP2PAlerts, operationSuccessful);
            isCommunicationWithOuinetInitiated = false;
            isOuinetStopRequestInitiated = false;
        }

        updateP2PSwitch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSavedInstanceStateCalled = false;

        if (pendingOuinetStatusOperationSuccessful != null) {
            Log.v(LOGTAG, "onResume: calling onOuinetStatusUpdate since pendingOuinetStatusOperationSuccessful is not null = " + pendingOuinetStatusOperationSuccessful);
            onOuinetStatusUpdate(pendingOuinetStatusOperationSuccessful);
        }

        updateP2PSwitch();
    }

    private void updateP2PSwitch() {
        if (!isOuinetStopRequestInitiated) {
            // We do not call ouinetSwitch.setChecked if the user initiated Ouinet stop request since the OuinetService
            // might still be running and so in case of orientation change if we execute  the following statement it will set the P2P switch to a wrong value (i.e. true / ON).
            if (PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
                setOuinetPref(true);
                mP2PSwitch.setChecked(true);
            } else {
                setOuinetPref(false);
                mP2PSwitch.setChecked(false);
            }
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
                case R.id.nav_p2p:
                    // Do nothing as there is already an item click listener defined for this item in this class. Also, do not close nav panel and so return from here.
                    return true;
                case R.id.nav_p2p_settings:
                    startActivity(new Intent(this, P2PActivity.class));
                    break;
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

    private void setLatestVersionCodeDownloadInfo(Version version) {
        AppDownloadInfoForVersionCode highestVersionCodeAppDownloadInfo = null;
        Integer highestVersionCode = -1;
        if(version.appDownloadInfoForVersionCodes != null) {
            for (AppDownloadInfoForVersionCode appDownloadInfoForVersionCode : version.appDownloadInfoForVersionCodes) {
                if (appDownloadInfoForVersionCode.versionCode.compareTo(highestVersionCode) > 0) {
                    highestVersionCode = appDownloadInfoForVersionCode.versionCode;
                    highestVersionCodeAppDownloadInfo = appDownloadInfoForVersionCode;
                }
            }
        }
        if (highestVersionCodeAppDownloadInfo != null) {
            version.setCurrentAppDownloadInfo(highestVersionCodeAppDownloadInfo);
        }
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

                    setLatestVersionCodeDownloadInfo(paskoocheh);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mOuinetStatusReceiver);
    }
}
