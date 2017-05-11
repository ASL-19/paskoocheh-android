package org.asl19.paskoocheh.toollist;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.baseactivities.BaseNavigationActivity;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.source.DownloadCountRepository;
import org.asl19.paskoocheh.data.source.RatingRepository;
import org.asl19.paskoocheh.data.source.ToolRepository;

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

        toolbarTitle.setText(getString(R.string.app_name));

        ToolListFragment toolListFragment =
                (ToolListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (toolListFragment == null) {
            toolListFragment = ToolListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), toolListFragment, R.id.contentFrame);
        }

        new ToolListPresenter(toolListFragment, new ToolRepository(getBaseContext(), getPackageManager()), new DownloadCountRepository(), new RatingRepository());
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_apps).setChecked(true);
    }
}
