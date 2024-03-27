package org.asl19.paskoocheh.gallery;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.asl19.paskoocheh.pojo.Image;

import java.util.List;

import static org.asl19.paskoocheh.gallery.ImageFragment.IMAGE_URL;
import static org.asl19.paskoocheh.gallery.ImageFragment.OUINET_GROUP;

/**
 * ImageAdapter for managing individual images for GalleryFragment.
 */
public class GalleryAdapter extends FragmentPagerAdapter {

    private List<Image> images;
    private String ouinetGroup;

    /**
     * Create new ImageAdapter instance.
     *
     * @param childFragmentManager The FragmentManager instance.
     */
    public GalleryAdapter(FragmentManager childFragmentManager, @NonNull List<Image> images, String ouinetGroup) {
        super(childFragmentManager);
        this.images = images;
        this.ouinetGroup = ouinetGroup;
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
        bundle.putString(OUINET_GROUP, ouinetGroup);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }
}
