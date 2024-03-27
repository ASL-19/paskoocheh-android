package org.asl19.paskoocheh.installedtoollist;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class InstalledToolListFragment extends InstallFragment implements InstalledToolListContract.ListView,
        InstalledToolListContract.ToolListAdapter {

    public static final String TAG = InstalledToolListFragment.class.getCanonicalName();
    private static final String DOWNLOAD_AND_RATING = "DOWNLOAD AND RATING";

    @BindView(R.id.installed_tools)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.update_all)
    TextView updateAllTextView;


    private List<Version> versions = new ArrayList<>();
    private List<LocalizedInfo> infoList = new ArrayList<>();
    private List<Images> imagesList = new ArrayList<>();

    private InstalledToolListContract.Presenter presenter;

    private InstalledToolListAdapter adapter;

    private Unbinder unbinder;

    public static InstalledToolListFragment newInstance() {
        return new InstalledToolListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_installed_tool_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(true);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        adapter = new InstalledToolListAdapter(this, versions, infoList, imagesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getInstalledTools();
        presenter.getLocalizedInfoList();
        presenter.getImages();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(InstalledToolListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onGetInstalledVersionListSuccessful(List<Version> versionList) {
        versions.clear();
        int appUpdates = 0;

        for (Version version: versionList) {
            version.setInstalled(false);
            version.setUpdateAvailable(false);

            try {
                int installedVersionCode = getContext().getPackageManager().getPackageInfo(version.getPackageName(), 0).versionCode;
                version.setInstalled(true);
                if (version.getVersionCode() > installedVersionCode) {
                    version.setUpdateAvailable(true);
                    appUpdates++;
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        if (appUpdates > 0) {
            updateAllTextView.setText(String.format("به\u200Cروزرسانی همه اپ\u200Cها (%s)", appUpdates));
            updateAllTextView.setBackgroundResource(R.drawable.button_blue);
            updateAllTextView.setTextColor(Color.WHITE);
        }

        versions.addAll(versionList);
        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onGetInstalledVersionListFailed() {

    }

    @Override
    public void onGetLocalizedInfoListSuccessful(List<LocalizedInfo> localizedInfoList) {
        this.infoList.addAll(localizedInfoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetLocalizedInfoListFailed() {

    }

    @Override
    public void onGetImageListSuccessful(List<Images> imagesList) {
        this.imagesList.addAll(imagesList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetImageListFailed() {

    }

    @OnClick(R.id.update_all)
    void updateAll() {
        for (Version version : versions) {
            if (version.isUpdateAvailable()) {
                installApplication(version);
            }
        }
    }

    @Override
    protected void onInstallSuccessUIUpdate() {
        version.setInstalled(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onInstallFailureUIUpdate() {
    }

    @Override
    public void onUpdateButtonClick(Version version, View updateButton) {
        installApplication(version);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
