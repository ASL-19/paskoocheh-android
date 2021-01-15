package org.asl19.paskoocheh.toollist;


import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;

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
