package org.asl19.paskoocheh.categorylist;


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
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.ToolLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.utils.AppExecutors;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryListActivity extends BaseUpActivity {

    public static final String TYPE = "TYPE";
    public static final String FEATURED = "FEATURED";
    public static final String TOP_DOWNLOADS = "TOP_DOWNLOADS";
    public static final String UPDATED = "UPDATED";
    public static final String CATEGORY = "CATEGORY";

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        Name name = null;
        String type = "";
        if (bundle != null) {
            name = Parcels.unwrap(bundle.getParcelable(CategoryListFragment.CATEGORY));
            type = bundle.getString(TYPE);
        }

        if (name != null) {
            toolbarTitle.setText(name.getFa());
            if (name.getFa().isEmpty()) {
                toolbarTitle.setText(name.getEn());
            }
        } else {
            if (type != null) {
                switch (type) {
                    case CategoryListActivity.FEATURED:
                        toolbarTitle.setText(getString(R.string.featured));
                        break;
                    case CategoryListActivity.UPDATED:
                        toolbarTitle.setText(getString(R.string.updated));
                        break;
                    case CategoryListActivity.TOP_DOWNLOADS:
                        toolbarTitle.setText(getString(R.string.most_downloads));
                        break;
                    default:
                        toolbarTitle.setText("");
                        break;
                }
            }
        }


        CategoryListFragment categoryListFragment =
                (CategoryListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (categoryListFragment == null) {
            categoryListFragment = CategoryListFragment.newInstance();
            categoryListFragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), categoryListFragment, R.id.contentFrame);
        }

        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
        AppExecutors appExecutors = new AppExecutors();
        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        DownloadAndRatingLocalDataSource downloadAndRatingLocalDataSource = DownloadAndRatingLocalDataSource.getInstance(appExecutors, database.downloadAndRatingDao());
        ImagesDataSource imagesDataSource = ImagesLocalDataSource.getInstance(appExecutors, database.imagesDao());
        LocalizedInfoDataSource localizedInfoDataSource = LocalizedInfoLocalDataSource.getInstance(appExecutors, database.localizedInfoDao());
        ToolDataSource toolDataSource = ToolLocalDataSource.getInstance(appExecutors, database.toolDao());

        switch (type) {
            case CategoryListActivity.FEATURED:
                new FeaturedListPresenter(categoryListFragment, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource, toolDataSource);
                break;
            case CategoryListActivity.UPDATED:
                new UpdatedListPresenter(categoryListFragment, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource);
                break;
            case CategoryListActivity.TOP_DOWNLOADS:
                new DownloadedListPresenter(categoryListFragment, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource);
                break;
            default:
                new CategoryListPresenter(categoryListFragment, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource);
                break;
        }
    }
}
