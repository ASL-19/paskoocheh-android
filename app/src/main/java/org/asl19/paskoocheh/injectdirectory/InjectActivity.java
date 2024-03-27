package org.asl19.paskoocheh.injectdirectory;

import android.os.Bundle;

import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InjectActivity extends BaseUpActivity {
    public static final String EXTRA_IS_GO_BACK_TO_HOME_AFTER_INJECT = "org.asl19.paskoocheh.injectdirectory.InjectActivity.EXTRA_IS_GO_BACK_TO_HOME_AFTER_INJECT";

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));
        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.inject_directory));
        InjectFragment injectFragment =
                (InjectFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (injectFragment == null) {
            boolean isGoBackToHome = getIntent().getBooleanExtra(EXTRA_IS_GO_BACK_TO_HOME_AFTER_INJECT, true);
            injectFragment = injectFragment.newInstance(isGoBackToHome);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), injectFragment, R.id.contentFrame);
        }

        new InjectPresenter(injectFragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}