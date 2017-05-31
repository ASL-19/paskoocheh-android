package org.asl19.paskoocheh.gallery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    public static final String IMAGE_URL = "IMAGE_URL";

    /**
     * ImageFragment.
     */
    public ImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View imageLayout = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.gallery_image);
        String image = getArguments().getString(IMAGE_URL, "");
        if (image.isEmpty()) {
            image = null;
        }
        Picasso.with(getContext()).load(image).into(imageView);

        return imageLayout;
    }
}
