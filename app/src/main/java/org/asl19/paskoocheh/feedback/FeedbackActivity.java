package org.asl19.paskoocheh.feedback;


import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.data.source.AmazonRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends BaseUpActivity {
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.support));

        FeedbackFragment feedback =
                (FeedbackFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (feedback == null) {
            feedback = FeedbackFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), feedback, R.id.contentFrame);
        }

        new FeedbackPresenter(feedback, new AmazonRepository(getApplicationContext()));
    }
}
