package org.asl19.paskoocheh.installedtoollist;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseNavigationActivity;
import org.asl19.paskoocheh.data.source.DownloadCountRepository;
import org.asl19.paskoocheh.data.source.RatingRepository;
import org.asl19.paskoocheh.data.source.ToolRepository;

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

        toolbarTitle.setText(R.string.app_name);

        InstalledToolListFragment installedToolListFragment =
                (InstalledToolListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (installedToolListFragment == null) {
            installedToolListFragment = InstalledToolListFragment.newInstance();
            installedToolListFragment.setArguments(getIntent().getExtras());
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), installedToolListFragment, R.id.contentFrame);
        }

        new InstalledToolListPresenter(installedToolListFragment, new ToolRepository(getBaseContext(), getPackageManager()), new DownloadCountRepository(), new RatingRepository());
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_installed_apps).setChecked(true);
    }
}