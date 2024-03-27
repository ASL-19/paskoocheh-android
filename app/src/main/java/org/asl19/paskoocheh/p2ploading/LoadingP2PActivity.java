package org.asl19.paskoocheh.p2ploading;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.baseactivities.BaseNavigationActivity;
import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingP2PActivity extends BaseNavigationActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.apps));

        LoadingP2PFragment loadingP2PFragment =
                (LoadingP2PFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (loadingP2PFragment == null) {
            loadingP2PFragment = LoadingP2PFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), loadingP2PFragment, R.id.contentFrame);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppsConfigDownloadComplete(Event.AppsConfigComplete e) {
        PaskoochehConfigService.startLoadingOtherConfigs(this);
        ToolListActivity.start(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
