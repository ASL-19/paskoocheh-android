package org.asl19.paskoocheh.installedtoollist;


import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseNavigationActivity;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.Local.ImagesLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.LocalizedInfoLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.utils.AppExecutors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstalledToolListActivity extends BaseNavigationActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(R.string.my_applications);

        InstalledToolListFragment installedToolListFragment =
                (InstalledToolListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (installedToolListFragment == null) {
            installedToolListFragment = InstalledToolListFragment.newInstance();
            installedToolListFragment.setArguments(getIntent().getExtras());
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), installedToolListFragment, R.id.contentFrame);
        }


        AppExecutors appExecutors = new AppExecutors();
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        LocalizedInfoDataSource localizedInfoDataSource = LocalizedInfoLocalDataSource.getInstance(appExecutors, database.localizedInfoDao());
        ImagesDataSource imagesDataSource = ImagesLocalDataSource.getInstance(appExecutors, database.imagesDao());
        new InstalledToolListPresenter(installedToolListFragment, versionLocalDataSource, localizedInfoDataSource, imagesDataSource);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_installed_apps).setChecked(true);
    }
}