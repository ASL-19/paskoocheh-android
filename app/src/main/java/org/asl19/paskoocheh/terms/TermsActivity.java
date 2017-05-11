package org.asl19.paskoocheh.terms;


import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsActivity extends BaseUpActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.privacy_terms_title));

        TermsFragment about =
            (TermsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (about == null) {
            about = TermsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), about, R.id.contentFrame);
        }
    }
}
