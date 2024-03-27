package org.asl19.paskoocheh.gallery;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String OUINET_GROUP = "OUINET_GROUP";

    /**
     * ImageFragment.
     */
    public ImageFragment() {
    }

    @NonNull
    protected OkHttpClient getPicassoClient(String ouinetGroup) {
        Interceptor addOuinetGroup = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request()
                        .newBuilder()
                        .addHeader("X-Ouinet-Group", ouinetGroup)
                        .build();
                return chain.proceed(newRequest);
            }
        };

        return PaskoochehApplication.getInstance()
            .getOkHttpClientBuilder(addOuinetGroup)
            .build();
    }

    @NonNull
    protected Picasso getPicasso(String ouinetGroup) {
        if (ouinetGroup == null)
            return Picasso.with(getContext());

        return new Picasso.Builder(getContext())
            .downloader(new OkHttp3Downloader(getPicassoClient(ouinetGroup)))
            .build();
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
        String ouinetGroup = getArguments().getString(OUINET_GROUP, null);
        getPicasso(ouinetGroup).load(image).into(imageView);

        return imageLayout;
    }
}
