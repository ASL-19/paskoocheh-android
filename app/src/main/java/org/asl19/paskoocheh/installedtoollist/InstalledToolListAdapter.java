package org.asl19.paskoocheh.installedtoollist;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Image;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toolinfo.ToolInfoActivity;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.toollist.ToolListFragment;
import org.asl19.paskoocheh.utils.ApkManager;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.FA;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class InstalledToolListAdapter extends RecyclerView.Adapter<InstalledToolListAdapter.ViewHolder> {

    private List<Version> versions;
    private List<LocalizedInfo> localizedInfoList;
    private List<Images> imagesList;
    private InstalledToolListContract.ToolListAdapter fragment;
    private Context context;
    private ApkManager apkManager;

    public InstalledToolListAdapter(InstalledToolListContract.ToolListAdapter fragment, List<Version> versions, List<LocalizedInfo> localizedInfoList, List<Images> imagesList) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.versions = versions;
        this.localizedInfoList = localizedInfoList;
        this.imagesList = imagesList;
        this.apkManager = new ApkManager(context.getApplicationContext());
    }

    @Override
    public InstalledToolListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_tool_installed, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Version version = versions.get(position);

        Image loadImage = null;
        for (Images image: imagesList) {
            if (image.getVersionId() == version.getId() && !image.getLogo().isEmpty()) {
                loadImage = image.getLogo().isEmpty() ? null : image.getLogo().get(0);
                break;
            } else if (image.getToolId() == version.getToolId() && !image.getLogo().isEmpty()) {
                loadImage = image.getLogo().isEmpty() ? null : image.getLogo().get(0);
            }
        }

        if (loadImage != null) {
            Picasso.with(context)
                    .load(loadImage.getUrl())
                    .into(holder.imageView);
        }

        LocalizedInfo localizedInfoTemp = new LocalizedInfo();
        for (LocalizedInfo localizedInfo: localizedInfoList) {
            if (localizedInfo.getToolId() == version.getToolId()) {
                if (localizedInfo.getLocale().equals(FA)) {
                    if (!localizedInfo.getName().isEmpty()) {
                        localizedInfoTemp.setName(localizedInfo.getName());
                    }

                    if (!localizedInfo.getDescription().isEmpty()) {
                        String text = HtmlRenderer.builder().build().render(Parser.builder().build().parse(localizedInfo.getDescription()));
                        text = text.replace("</li>", System.getProperty("line.separator"));
                        text = text.replace("<li>", " \u2022 ");
                        localizedInfoTemp.setDescription(text);
                    }
                } else {
                    if (localizedInfoTemp.getName().isEmpty()) {
                        localizedInfoTemp.setName(localizedInfo.getName());
                    }

                    if (localizedInfo.getDescription().isEmpty()) {
                        String text = HtmlRenderer.builder().build().render(Parser.builder().build().parse(localizedInfo.getDescription()));
                        text = text.replace("</li>", System.getProperty("line.separator"));
                        text = text.replace("<li>", " \u2022 ");
                        localizedInfoTemp.setDescription(text);                    }
                }
            }
        }

        if (!localizedInfoTemp.getName().isEmpty()) {
            version.setAppName(localizedInfoTemp.getName());
        }

        holder.name.setText(version.getAppName());

        if (!localizedInfoTemp.getDescription().isEmpty()) {
            holder.detailsText.setText(Html.fromHtml(HtmlRenderer.builder().build().render(Parser.builder().build().parse(localizedInfoTemp.getDescription()))));
        }

        holder.version.setText(String.format(context.getString(R.string.version_installed), version.getVersionNumber()));
        holder.size.setText(Formatter.formatFileSize(context, version.getSize()));
        holder.detailsDate.setText(version.getReleaseJDate());

        holder.buttonLayout.setBackgroundResource(R.drawable.button_blue);
        holder.updateTextView.setVisibility(GONE);
        holder.updatingProgressBar.setVisibility(GONE);
        holder.upToDateImageView.setVisibility(GONE);

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

        if (version.isUpdateAvailable()) {
            holder.updateTextView.setVisibility(VISIBLE);
            holder.buttonLayout.setBackgroundResource(R.drawable.button_regular_blue);
        } else {
            holder.upToDateImageView.setVisibility(VISIBLE);
            holder.buttonLayout.setBackgroundResource(R.drawable.button_blue);
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
        @BindView(R.id.version)
        TextView version;
        @BindView(R.id.size)
        TextView size;
        @BindView(R.id.button_layout)
        LinearLayout buttonLayout;
        @BindView(R.id.up_to_date)
        ImageView upToDateImageView;
        @BindView(R.id.update)
        TextView updateTextView;
        @BindView(R.id.updating)
        ProgressBar updatingProgressBar;
        @BindView(R.id.detail_arrow)
        ImageView detailArrow;
        @BindView(R.id.details_layout)
        LinearLayout detailLayout;
        @BindView(R.id.details_date)
        TextView detailsDate;
        @BindView(R.id.details_text)
        TextView detailsText;


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
            Version tool = versions.get(getLayoutPosition());
            fragment.onUpdateButtonClick(tool, updateTextView);
        }

        @OnClick(R.id.more_details)
        void moreDetails() {
            if (detailLayout.getVisibility() == VISIBLE) {
                detailLayout.setVisibility(GONE);
                detailArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_blue);

            } else {
                detailLayout.setVisibility(VISIBLE);
                detailArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_blue);
            }
        }

        @Override
        public void onClick(View view) {
            Version version = versions.get(getLayoutPosition());

            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
            bundle.putString(TOOL_ID, version.getAppName());
            FirebaseAnalytics.getInstance(context).logEvent(Constants.TOOL_SELECT, bundle);

            Intent intent = new Intent(context, ToolInfoActivity.class);
            intent.putExtra(ToolInfoFragment.TOOL, versions.get(getLayoutPosition()).getToolId());
            intent.putExtra(ToolInfoActivity.TOOL_NAME, versions.get(getLayoutPosition()).getAppName());
            context.startActivity(intent);
        }
    }
}
