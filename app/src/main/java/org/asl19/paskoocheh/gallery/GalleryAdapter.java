package org.asl19.paskoocheh.gallery;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.asl19.paskoocheh.pojo.Image;

import java.util.List;

import static org.asl19.paskoocheh.gallery.ImageFragment.IMAGE_URL;

/**
 * ImageAdapter for managing individual images for GalleryFragment.
 */
public class GalleryAdapter extends FragmentPagerAdapter {

    private List<Image> images;

    /**
     * Create new ImageAdapter instance.
     *
     * @param childFragmentManager The FragmentManager instance.
     */
    public GalleryAdapter(FragmentManager childFragmentManager, @NonNull List<Image> images) {
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
        bundle.putString(IMAGE_URL, images.get(position).url);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }
}
