package org.asl19.paskoocheh.installedtoollist;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.utils.ApkManager;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class InstalledToolListFragment extends Fragment implements InstalledToolListContract.ListView,
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
                updateApplication(version);
            }
        }
    }

    void updateApplication(Version version) {
        if (!version.getDownloadVia().getS3().equals("https://s3.amazonaws.com/paskoocheh-repo")) {
            installApplication(version);
        } else if (!version.getDownloadVia().getUrl().isEmpty()) {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(version.getDownloadVia().getUrl())
            );

            startActivity(browserIntent);
        } else {
            playStoreRedirect(version);
        }
    }

    void playStoreRedirect(Version version) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, InstalledToolListFragment.TAG);
        bundle.putString(TOOL_ID, version.getAppName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.PLAY_STORE, bundle);

        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + version.getPackageName())
        );

        getContext().startActivity(browserIntent);
    }

    void installApplication(Version version) {
        File toolFile = new File(getContext().getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk", version.getAppName(), version.getVersionNumber()));

        if (toolFile.exists()) {
            new ApkManager(getContext()).installPackage(version, toolFile);
        } else {
            ConnectivityManager connManager
                    = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (!getContext().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                Intent intent = new Intent(getContext(), ToolDownloadService.class);
                intent.putExtra("VERSION", Parcels.wrap(version));
                getContext().startService(intent);
                Toast.makeText(getContext(), getString(R.string.queued), Toast.LENGTH_SHORT).show();
            } else if ((activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)) {
                Toast.makeText(getContext(), getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
