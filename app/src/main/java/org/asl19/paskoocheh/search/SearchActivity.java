package org.asl19.paskoocheh.search;


import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.Local.DownloadAndRatingLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.ImagesLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.LocalizedInfoLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.NameLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.utils.AppExecutors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseUpActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(R.string.search_title);

        SearchFragment searchFragment =
                (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), searchFragment, R.id.contentFrame);
        }


        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
        AppExecutors appExecutors = new AppExecutors();
        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        DownloadAndRatingLocalDataSource downloadAndRatingLocalDataSource = DownloadAndRatingLocalDataSource.getInstance(appExecutors, database.downloadAndRatingDao());
        ImagesDataSource imagesLocalDataSource = ImagesLocalDataSource.getInstance(appExecutors, database.imagesDao());
        LocalizedInfoDataSource localizedInfoLocalDataSource = LocalizedInfoLocalDataSource.getInstance(appExecutors, database.localizedInfoDao());
        NameDataSource nameLocalDataSource = NameLocalDataSource.getInstance(appExecutors, database.nameDao());
        new SearchPresenter(searchFragment, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesLocalDataSource, localizedInfoLocalDataSource, nameLocalDataSource);
    }
}
