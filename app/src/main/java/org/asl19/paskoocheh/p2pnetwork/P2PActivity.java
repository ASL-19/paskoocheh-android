package org.asl19.paskoocheh.p2pnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;


import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.toollist.ToolListActivity;
import org.asl19.paskoocheh.utils.AppExecutors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class P2PActivity extends BaseUpActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.p2p_settings));

        P2PFragment p2pFragment =
                (P2PFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (p2pFragment == null) {
            p2pFragment = p2pFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), p2pFragment, R.id.contentFrame);
        }

        AppExecutors appExecutors = new AppExecutors();
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        //TextDataSource textLocalDataSource = TextLocalDataSource.getInstance(appExecutors, database.textDao());
        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        //LocalizedInfoDataSource localizedInfoDataSource = LocalizedInfoLocalDataSource.getInstance(appExecutors, database.localizedInfoDao());
       // ImagesDataSource imagesDataSource = ImagesLocalDataSource.getInstance(appExecutors, database.imagesDao());

        new P2PPresenter(p2pFragment, versionLocalDataSource);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // This method is called when the up button is pressed.
        startActivity(new Intent(this, ToolListActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ToolListActivity.class));
        finish();
    }
}
