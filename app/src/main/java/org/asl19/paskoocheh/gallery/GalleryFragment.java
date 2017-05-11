package org.asl19.paskoocheh.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.asl19.paskoocheh.R;

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

    private static int position;
    private static List<String> toolImages;


    /**
     * GalleryFragment.
     */
    public GalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(POSITION);
        toolImages = getArguments().getStringArrayList(IMAGES);

        position = toolImages.size() - 1 - position;
        Collections.reverse(toolImages);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View galleryView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ViewPager pager = (ViewPager) galleryView.findViewById(R.id.pager);
        pager.setAdapter(new GalleryAdapter(getChildFragmentManager(), toolImages));
        pager.setCurrentItem(position);

        return galleryView;
    }
}
