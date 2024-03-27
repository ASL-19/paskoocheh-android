package org.asl19.paskoocheh.toollist;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Image;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toolinfo.ToolInfoActivity;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.utils.ApkManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.FA;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PACKAGE;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class ToolListAdapter extends RecyclerView.Adapter<ToolListAdapter.ViewHolder> {

    private List<Version> versions;
    private List<DownloadAndRating> downloadAndRatings;
    private List<Images> images;
    private List<LocalizedInfo> localizedInfos;
    private Integer cardId;
    private ToolListContract.ToolListAdapter fragment;
    private Context context;
    private Picasso picasso;
    private ApkManager apkManager;

    public ToolListAdapter(ToolListContract.ToolListAdapter fragment, List<Version> versions, List<DownloadAndRating> downloadAndRatings, List<Images> images, List<LocalizedInfo> localizedInfos, int cardId) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.versions = versions;
        this.downloadAndRatings = downloadAndRatings;
        this.localizedInfos = localizedInfos;
        this.images = images;
        this.cardId = cardId;
        this.apkManager = new ApkManager(context.getApplicationContext());
    }

    @Override
    public ToolListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(cardId, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Version version = versions.get(position);

        Image loadImage = null;
        for (Images image: this.images) {
            if (image.getVersionId() == version.getId() && !image.getLogo().isEmpty()) {
                loadImage = image.getLogo().isEmpty() ? null : image.getLogo().get(0);
                break;
            } else if (image.getToolId() == version.getToolId() && !image.getLogo().isEmpty()) {
                loadImage = image.getLogo().isEmpty() ? null : image.getLogo().get(0);
            }
        }

        if (loadImage != null) {
            holder.imageView.setPadding(0,0,0,0);
            if (!loadImage.isFullBleed()) {
                holder.imageView.setPadding(16,16,16,16);
            }

            getPicasso()
                .load(loadImage.getUrl())
                .into(holder.imageView);
        }

        LocalizedInfo localizedInfoTemp = new LocalizedInfo();
        for (LocalizedInfo localizedInfo: localizedInfos) {
            if (localizedInfo.getToolId() == version.getToolId()) {
                if (localizedInfo.getLocale().equals(FA)) {
                    if (!localizedInfo.getName().isEmpty()) {
                        localizedInfoTemp.setName(localizedInfo.getName());
                    }
                } else {
                    if (localizedInfoTemp.getName().isEmpty()) {
                        localizedInfoTemp.setName(localizedInfo.getName());
                    }
                }
            }
        }

        if (!localizedInfoTemp.getName().isEmpty()) {
            version.setAppName(localizedInfoTemp.getName());
        }

        holder.name.setText(version.getAppName());

        holder.downloadIcon.setVisibility(View.INVISIBLE);
        holder.downloadTextView.setVisibility(View.INVISIBLE);
        holder.ratingIcon.setVisibility(GONE);
        holder.rating.setText("");

        if (downloadAndRatings != null) {
            for (DownloadAndRating downloadAndRating : downloadAndRatings) {
                if (downloadAndRating.getToolId().equals(version.getToolId())) {

                    if (downloadAndRating.getDownloadCount() != null) {
                        holder.downloadIcon.setVisibility(VISIBLE);
                        holder.downloadTextView.setVisibility(VISIBLE);
                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("fa"));
                        holder.downloadTextView.setText(String.valueOf(formatter.format(downloadAndRating.getDownloadCount())));
                    }

                    if (downloadAndRating.getRating() != null) {
                        holder.ratingIcon.setVisibility(VISIBLE);
                        holder.rating.setText(String.valueOf(downloadAndRating.getRating()));
                    }
                    break;
                }
            }
        }

        holder.updateTextView.setVisibility(GONE);
        holder.installTextView.setVisibility(GONE);
        holder.playStoreTextView.setVisibility(GONE);
        holder.installedLayout.setVisibility(VISIBLE);

        version.setUpdateAvailable(false);
        version.setInstalled(false);
        try {
            int installedVersionCode = context.getPackageManager().getPackageInfo(version.getPackageName(), 0).versionCode;
            version.setInstalled(true);
            if (version.getVersionCode() > installedVersionCode) {
                version.setUpdateAvailable(true);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (!version.isInstalled()) {
            holder.installedLayout.setVisibility(GONE);
            if (version.getDownloadVia() != null && !version.getDownloadVia().getS3().isEmpty() || !version.getDownloadVia().getUrl().isEmpty()) {
                holder.installTextView.setVisibility(VISIBLE);
            } else {
                holder.playStoreTextView.setVisibility(VISIBLE);
            }
        }

        if (version.isUpdateAvailable()) {
            holder.installedLayout.setVisibility(GONE);
            holder.updateTextView.setVisibility(VISIBLE);
        }

        if (version.getPackageName() != null &&
                version.getPackageName().equals(PASKOOCHEH_PACKAGE)) {
            holder.installedLayout.setVisibility(GONE);
        }
    }

    @NonNull
    protected static OkHttpClient getPicassoClient() {
        Interceptor addOuinetGroup = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request oldRequest = chain.request();
                // Encoded OkHttp paths always start with a slash.
                String resourcePath = pathComponent(oldRequest.url().encodedPath().substring(1));
                Request newRequest = oldRequest
                        .newBuilder()
                        .addHeader("X-Ouinet-Group", resourcePath)
                        .build();
                return chain.proceed(newRequest);
            }
        };

        return PaskoochehApplication.getInstance()
            .getOkHttpClientBuilder(addOuinetGroup)
            .build();
    }

    @NonNull
    protected static String pathComponent(String pathResource) {
        /*
        Check if pathResource starts with BUCKET_NAME(paskoocheh-repo or paskoocheh-dev|staging-storage),
        if not concatenate with BUCKET_NAME
        */
        if (!pathResource.startsWith(BUCKET_NAME)) {
            pathResource = BUCKET_NAME + "/" + pathResource;
        }
        /*Remove the filename from the path
         */
        int pos = pathResource.lastIndexOf('/');
        if (pos > -1) {
            pathResource = pathResource.substring(0, pos);
        }

        return pathResource;
    }

    @NonNull
    protected Picasso getPicasso() {
        synchronized (this) {
            if (picasso == null)
                picasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(getPicassoClient()))
                    .build();
        }
        return picasso;
    }

    @Override
    public int getItemCount() {
        return (null != versions ? versions.size() : 0);
    }

    /**
     * Custom RecyclerView.ViewHolder implementation.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.title)
        TextView name;
        @BindView(R.id.install)
        TextView installTextView;
        @BindView(R.id.update)
        TextView updateTextView;
        @BindView(R.id.rating)
        TextView rating;
        @BindView(R.id.star)
        ImageView ratingIcon;
        @BindView(R.id.installed)
        LinearLayout installedLayout;
        @BindView(R.id.play_store)
        TextView playStoreTextView;
        @BindView(R.id.download_icon)
        ImageView downloadIcon;
        @BindView(R.id.download)
        TextView downloadTextView;

        /**
         * ViewHolder for View with Android Tools
         *
         * @param view The view.
         */
        public ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @OnClick(R.id.install)
        void installApplication() {
            Version tool = versions.get(getLayoutPosition());
            fragment.onInstallButtonClick(tool, installTextView);
        }

        @OnClick(R.id.update)
        void updateApplication() {
            Version tool = versions.get(getLayoutPosition());
            fragment.onUpdateButtonClick(tool, updateTextView);
        }

        @OnClick(R.id.play_store)
        void playStoreRedirect() {
            Version version = versions.get(getLayoutPosition());
            fragment.onPlayStoreRedirectButtonClick(version);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
            bundle.putString(TOOL_ID, versions.get(getLayoutPosition()).getAppName());
            FirebaseAnalytics.getInstance(context).logEvent(Constants.TOOL_SELECT, bundle);

            Intent intent = new Intent(context, ToolInfoActivity.class);
            intent.putExtra(ToolInfoFragment.TOOL, versions.get(getLayoutPosition()).getToolId());
            intent.putExtra(ToolInfoActivity.TOOL_NAME, versions.get(getLayoutPosition()).getAppName());
            context.startActivity(intent);
        }
    }
}
