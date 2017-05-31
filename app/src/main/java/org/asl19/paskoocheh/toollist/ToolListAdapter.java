package org.asl19.paskoocheh.toollist;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.DownloadCount;
import org.asl19.paskoocheh.pojo.Rating;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.toolinfo.ToolInfoActivity;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.utils.ApkManager;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PACKAGE;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class ToolListAdapter extends RecyclerView.Adapter<ToolListAdapter.ViewHolder> implements Filterable {

    private List<AndroidTool> tools;
    private List<AndroidTool> filteredAndroidTools;
    private List<DownloadCount> downloadCountList;
    private List<Rating> ratingList;
    private Integer cardId;
    private ToolListContract.ToolListAdapter fragment;
    private Context context;

    public ToolListAdapter(ToolListContract.ToolListAdapter fragment, List<AndroidTool> tools, List<DownloadCount> downloadCountList, List<Rating> ratingList, int cardId) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.tools = tools;
        this.filteredAndroidTools = new ArrayList<>(tools);
        this.downloadCountList = downloadCountList;
        this.ratingList = ratingList;
        this.cardId = cardId;
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
        AndroidTool tool = filteredAndroidTools.get(position);

        Picasso.with(context).load(tool.getIconUrl().isEmpty() ? null : tool.getIconUrl()).into(holder.imageView);

        holder.name.setText(tool.getName());
        holder.description.setText(tool.getAppType());

        holder.downloadIcon.setVisibility(View.INVISIBLE);
        holder.downloadTextView.setVisibility(View.INVISIBLE);
        holder.ratingIcon.setVisibility(GONE);
        holder.rating.setText("");

        if (downloadCountList != null) {
            for (DownloadCount downloadCount : downloadCountList) {
                if (downloadCount.getPlatform().equals(Constants.ANDROID) && downloadCount.getAppName().equals(tool.getEnglishName())) {
                    holder.downloadIcon.setVisibility(VISIBLE);
                    holder.downloadTextView.setVisibility(VISIBLE);
                    holder.downloadTextView.setText(downloadCount.getDownloadCount().toString());
                    break;
                }
            }
        }

        if (ratingList != null) {
            for (Rating rating : ratingList) {
                if (rating.getAppName().equals(tool.getEnglishName())) {
                    holder.ratingIcon.setVisibility(VISIBLE);
                    holder.rating.setText(rating.getRating());
                    break;
                }
            }
        }

        holder.updateTextView.setVisibility(GONE);
        holder.installTextView.setVisibility(GONE);
        holder.playStoreTextView.setVisibility(GONE);
        holder.uninstallTextView.setVisibility(VISIBLE);

        tool.setUpdateAvailable(false);
        tool.setInstalled(false);
        try {
            int installedVersionCode = context.getPackageManager().getPackageInfo(tool.getPackageName(), 0).versionCode;
            tool.setInstalled(true);
            if (tool.getVersionCode() > installedVersionCode) {
                tool.setUpdateAvailable(true);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (!tool.isInstalled()) {
            holder.uninstallTextView.setVisibility(GONE);
            if (!tool.getDownloadUrl().isEmpty()) {
                holder.installTextView.setVisibility(VISIBLE);
            } else {
                holder.playStoreTextView.setVisibility(VISIBLE);
            }
        }

        if (tool.isUpdateAvailable()) {
            holder.updateTextView.setVisibility(VISIBLE);
        }

        if (tool.getPackageName().equals(PASKOOCHEH_PACKAGE)) {
            holder.uninstallTextView.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != filteredAndroidTools ? filteredAndroidTools.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return new ToolFilter(this, tools);
    }

    public final class ToolFilter extends Filter {

        private final ToolListAdapter adapter;
        private final List<AndroidTool> originalList;
        private final List<AndroidTool> filteredList;

        private ToolFilter(ToolListAdapter adapter, List<AndroidTool> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final AndroidTool tool : originalList) {
                    String toolName = tool.getName().toLowerCase().trim();
                    if (toolName.contains(filterPattern)) {
                        filteredList.add(tool);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredAndroidTools = (List<AndroidTool>) results.values;
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Custom RecyclerView.ViewHolder implementation.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.title)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.install)
        TextView installTextView;
        @BindView(R.id.update)
        TextView updateTextView;
        @BindView(R.id.rating)
        TextView rating;
        @BindView(R.id.star)
        ImageView ratingIcon;
        @BindView(R.id.uninstall)
        TextView uninstallTextView;
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

        @OnClick(R.id.update)
        void updateApplication() {
            AndroidTool tool = filteredAndroidTools.get(getLayoutPosition());
            if (tool.getDownloadUrl().isEmpty()) {
                playStoreRedirect();
            } else {
                installApplication();
            }
        }

        @OnClick(R.id.install)
        void installApplication() {
            AndroidTool tool = filteredAndroidTools.get(getLayoutPosition());
            File toolFile = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + tool.getName() + ".apk");
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                fragment.onPermissionRequested(tool.getToolId().intValue());
            } else if (toolFile.exists()) {
                fragment.registerInstall(tool.getEnglishName());
                ApkManager.installPackage(context, tool.getChecksum(), toolFile);
            } else {
                ConnectivityManager connManager
                        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
                if (!context.getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                    fragment.registerInstall(tool.getEnglishName());
                    Intent intent = new Intent(context, ToolDownloadService.class);
                    intent.putExtra("TOOL", Parcels.wrap(tool));
                    context.startService(intent);
                    Toast.makeText(context, context.getString(R.string.queued), Toast.LENGTH_SHORT).show();
                } else if ((activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)) {
                    Toast.makeText(context, context.getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @OnClick(R.id.play_store)
        void playStoreRedirect() {
            AndroidTool tool = filteredAndroidTools.get(getLayoutPosition());

            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
            bundle.putString(TOOL_ID, tool.getEnglishName());
            FirebaseAnalytics.getInstance(context).logEvent(Constants.PLAY_STORE, bundle);

            fragment.registerInstall(tool.getEnglishName());

            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + tool.getPackageName())
            );

            context.startActivity(browserIntent);
        }

        @OnClick(R.id.uninstall)
        void uninstallApplication() {
            ApkManager.uninstallPackage(context, filteredAndroidTools.get(getLayoutPosition()).getPackageName());
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
            bundle.putString(TOOL_ID, filteredAndroidTools.get(getLayoutPosition()).getEnglishName());
            FirebaseAnalytics.getInstance(context).logEvent(Constants.TOOL_SELECT, bundle);

            Intent intent = new Intent(context, ToolInfoActivity.class);
            intent.putExtra(ToolInfoFragment.TOOL, filteredAndroidTools.get(getLayoutPosition()).getToolId());
            intent.putExtra(ToolInfoFragment.RATING, rating.getText());
            intent.putExtra(ToolInfoFragment.DOWNLOAD_COUNT, downloadTextView.getText());
            context.startActivity(intent);
        }
    }
}
