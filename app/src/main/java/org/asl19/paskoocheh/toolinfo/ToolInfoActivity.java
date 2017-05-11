package org.asl19.paskoocheh.toolinfo;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.source.DownloadCountRepository;
import org.asl19.paskoocheh.data.source.RatingRepository;
import org.asl19.paskoocheh.data.source.ReviewRepository;
import org.asl19.paskoocheh.data.source.ToolRepository;
import org.asl19.paskoocheh.toollist.ToolListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToolInfoActivity extends BaseUpActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    private boolean deepLink = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(R.string.app_name);

        Bundle bundle = getIntent().getExtras();
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

        new ToolInfoPresenter(toolInfoFragment, new ToolRepository(getBaseContext(), getPackageManager()), new RatingRepository(), new DownloadCountRepository(), new ReviewRepository());
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
