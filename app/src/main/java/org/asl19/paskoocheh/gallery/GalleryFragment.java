package org.asl19.paskoocheh.gallery;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Image;
import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

/**
 * The GalleryFragment displays a rotating carousel
 * of images using the ImageAdapter.
 */
public class GalleryFragment extends Fragment {

    /**
     * GalleryFragment Tag.
     */
    public static final String TAG = "GalleryFragment";

    public static final String POSITION = "POSITION";
    public static final String IMAGES = "IMAGES";
    public static final String OUINET_GROUP = "OUINET_GROUP";

    private int position;
    private List<Image> toolImages;
    private String ouinetGroup;


    /**
     * GalleryFragment.
     */
    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = 0;
        ouinetGroup = "";
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION, 0);
            ouinetGroup = getArguments().getString(OUINET_GROUP, "");
        }

        toolImages = Parcels.unwrap(getArguments().getParcelable(IMAGES));
        Collections.reverse(toolImages);

        position = toolImages.size() - 1 - position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        View galleryView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ViewPager pager = galleryView.findViewById(R.id.pager);
        pager.setAdapter(new GalleryAdapter(getChildFragmentManager(), toolImages, ouinetGroup));
        pager.setCurrentItem(position);

        TabLayout tabLayout = galleryView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);

        return galleryView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigation_arrow);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigation_arrow_ltr);
        }
    }
}
