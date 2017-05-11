package org.asl19.paskoocheh.gallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import static org.asl19.paskoocheh.gallery.ImageFragment.IMAGE_URL;

/**
 * ImageAdapter for managing individual images for GalleryFragment.
 */
public class GalleryAdapter extends FragmentPagerAdapter {

    private List<String> images;

    /**
     * Create new ImageAdapter instance.
     *
     * @param childFragmentManager The FragmentManager instance.
     */
    public GalleryAdapter(FragmentManager childFragmentManager, @NonNull List<String> images) {
        super(childFragmentManager);
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Fragment getItem(int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IMAGE_URL, images.get(position));
        imageFragment.setArguments(bundle);
        return imageFragment;
    }
}
