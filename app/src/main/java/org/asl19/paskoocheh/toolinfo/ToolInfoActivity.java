package org.asl19.paskoocheh.toolinfo;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.FaqDataSource;
import org.asl19.paskoocheh.data.source.GuideDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.Local.DownloadAndRatingLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.FaqLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.GuideLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.ImagesLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.LocalizedInfoLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.NameLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.ReviewLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.ToolLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.TutorialLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.data.source.TutorialDataSource;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.asl19.paskoocheh.utils.AppExecutors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToolInfoActivity extends BaseUpActivity {

    public static final String TOOL_NAME = "TOOL_NAME";
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    private boolean deepLink = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            toolbarTitle.setText(bundle.getString(TOOL_NAME));
        }

        Uri uri = getIntent().getData();

        if (uri != null) {
            deepLink = true;
            String[] segments = uri.getPath().split("/");
            String idStr = segments[segments.length - 2];
            try {
                bundle.putLong(ToolInfoFragment.TOOL, Long.parseLong(idStr));
            } catch (NumberFormatException exception) {
                Log.e(getClass().getName(), exception.toString());
            }
        }

        ToolInfoFragment toolInfoFragment =
                (ToolInfoFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (toolInfoFragment == null) {
            toolInfoFragment = ToolInfoFragment.newInstance();
            toolInfoFragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), toolInfoFragment, R.id.contentFrame);
        }

        AppExecutors appExecutors = new AppExecutors();
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        ReviewLocalDataSource reviewLocalDataSource = ReviewLocalDataSource.getInstance(appExecutors, database.reviewDao());
        DownloadAndRatingDataSource downloadAndRatingDataSource = DownloadAndRatingLocalDataSource.getInstance(appExecutors, database.downloadAndRatingDao());
        FaqDataSource faqDataSource = FaqLocalDataSource.getInstance(appExecutors, database.faqDao());
        LocalizedInfoDataSource localizedInfoDataSource = LocalizedInfoLocalDataSource.getInstance(appExecutors, database.localizedInfoDao());
        GuideDataSource guideDataSource = GuideLocalDataSource.getInstance(appExecutors, database.guideDao());
        ToolDataSource toolDataSource = ToolLocalDataSource.getInstance(appExecutors, database.toolDao());
        NameDataSource nameDataSource = NameLocalDataSource.getInstance(appExecutors, database.nameDao());
        ImagesDataSource imagesDataSource = ImagesLocalDataSource.getInstance(appExecutors, database.imagesDao());
        TutorialDataSource tutorialDataSource = TutorialLocalDataSource.getInstance(appExecutors, database.tutorialDao());

        new ToolInfoPresenter(toolInfoFragment, versionLocalDataSource, downloadAndRatingDataSource, reviewLocalDataSource, faqDataSource, localizedInfoDataSource, guideDataSource, toolDataSource, nameDataSource, imagesDataSource, tutorialDataSource);
    }

    @Override
    public void onBackPressed() {
        if (deepLink) {
            startActivity(new Intent(this, ToolListActivity.class));
        } else {
            super.onBackPressed();
        }
    }
}
