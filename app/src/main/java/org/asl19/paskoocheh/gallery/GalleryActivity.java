package org.asl19.paskoocheh.gallery;


import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.baseactivities.BaseUpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends BaseUpActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_content, (ViewGroup) findViewById(R.id.contentFrame));

        ButterKnife.bind(this);

        toolbarTitle.setText("");

        GalleryFragment gallery =
                (GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (gallery == null) {
            gallery = GalleryFragment.newInstance();
            gallery.setArguments(getIntent().getExtras());
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), gallery, R.id.contentFrame);
        }
    }
}
