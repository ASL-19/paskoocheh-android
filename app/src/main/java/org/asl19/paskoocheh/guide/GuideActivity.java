package org.asl19.paskoocheh.guide;


import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.data.source.GuideDataSource;
import org.asl19.paskoocheh.data.source.Local.GuideLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.utils.AppExecutors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideActivity extends BaseUpActivity {
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.guide));

        GuideFragment guide =
                (GuideFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (guide == null) {
            guide = GuideFragment.newInstance();
            guide.setArguments(getIntent().getExtras());
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), guide, R.id.contentFrame);
        }

        PaskoochehDatabase paskoochehDatabase = PaskoochehDatabase.getInstance(getApplicationContext());
        GuideDataSource guideLocalDataSource = GuideLocalDataSource.getInstance(new AppExecutors(), paskoochehDatabase.guideDao());
        new GuidePresenter(guide, guideLocalDataSource);
    }
}
