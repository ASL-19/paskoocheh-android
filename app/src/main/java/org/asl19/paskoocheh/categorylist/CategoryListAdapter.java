package org.asl19.paskoocheh.categorylist;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Image;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.toolinfo.ToolInfoActivity;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.toollist.ToolListFragment;
import org.asl19.paskoocheh.utils.ApkManager;
import org.parceler.Parcels;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.FA;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PACKAGE;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private List<Version> versions;
    private List<DownloadAndRating> downloadAndRatings;
    private List<Images> images;
    private List<LocalizedInfo> localizedInfos;
    private Integer cardId;
    private CategoryListContract.CategoryListAdapter fragment;
    private Context context;
    private ApkManager apkManager;

    public CategoryListAdapter(CategoryListContract.CategoryListAdapter fragment, List<Version> versions, List<DownloadAndRating> downloadAndRatings, List<Images> images, List<LocalizedInfo> localizedInfos, int cardId) {
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
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                loadImage = image.getLogo().isEmpty() ? new Image() : image.getLogo().get(0);
                break;
            } else if (image.getToolId() == version.getToolId() && !image.getLogo().isEmpty()) {
                loadImage = image.getLogo().isEmpty() ? new Image() : image.getLogo().get(0);
            }
        }

        if (loadImage != null) {
            holder.imageView.setPadding(0,0,0,0);
            if (!loadImage.isFullBleed()) {
                holder.imageView.setPadding(16,16,16,16);
            }

            Picasso.with(context)
                    .load(loadImage.getUrl())
                    .into(holder.imageView);
        }

        LocalizedInfo localizedInfoTemp = new LocalizedInfo();
        for (LocalizedInfo localizedInfo: localizedInfos) {
            if (localizedInfo.getToolId() == version.getToolId()) {
                if (localizedInfo.getLocale().equals(FA)) {
                    localizedInfoTemp = localizedInfo;
                    break;
                } else if (localizedInfoTemp == null) {
                    localizedInfoTemp = localizedInfo;
                }
                break;
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
                        holder.downloadTextView.setText(String.valueOf(formatter.format(downloadAndRating.getDownloadCount())));                    }

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

        @OnClick({R.id.update, R.id.install})
        void installApplication() {
            Version tool = versions.get(getLayoutPosition());
            if (!tool.getDownloadVia().getS3().equals("https://s3.amazonaws.com/paskoocheh-repo")) {
                installS3();
            } else if (!tool.getDownloadVia().getUrl().isEmpty()) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(tool.getDownloadVia().getUrl())
                );
                if(browserIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(browserIntent);
                } else {
                    FirebaseCrashlytics.getInstance().log(tool.appName + " has a bad URI (" + tool.getDownloadVia().getUrl() + ") ");
                    playStoreRedirect();
                }
            } else {
                playStoreRedirect();
            }
        }

        void installS3() {
            Version version = versions.get(getLayoutPosition());
            File toolFile = new File(context.getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk", version.getAppName(), version.getVersionNumber()));
            if (toolFile.exists()) {
                apkManager.installPackage(version, toolFile);
            } else {
                ConnectivityManager connManager
                        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
                if (!context.getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                    Intent intent = new Intent(context, ToolDownloadService.class);
                    intent.putExtra("VERSION", Parcels.wrap(version));
                    context.startService(intent);
                    Toast.makeText(context, context.getString(R.string.queued), Toast.LENGTH_SHORT).show();
                } else if ((activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)) {
                    Toast.makeText(context, context.getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @OnClick(R.id.play_store)
        void playStoreRedirect() {
            Version version = versions.get(getLayoutPosition());

            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
            bundle.putString(TOOL_ID, version.getAppName());
            FirebaseAnalytics.getInstance(context).logEvent(Constants.PLAY_STORE, bundle);

            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + version.getPackageName())
            );

            context.startActivity(browserIntent);
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
