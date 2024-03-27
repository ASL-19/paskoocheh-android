package org.asl19.paskoocheh.categorylist;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Version;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CategoryListFragment extends InstallFragment implements CategoryListContract.CategoryListView, CategoryListContract.CategoryListAdapter {

    public static final String TAG = CategoryListFragment.class.getCanonicalName();
    public static final String CATEGORY = "CATEGORY";

    @BindView(R.id.my_recycler_all_apps)
    RecyclerView recyclerAllApps;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tools_layout)
    LinearLayout toolsLayout;

    private CategoryListAdapter adapterAllApps;

    private List<Version> androidVersions = new ArrayList<>();
    private List<DownloadAndRating> downloadAndRatings = new ArrayList<>();
    private List<Images> images = new ArrayList<>();
    private List<LocalizedInfo> localizedInfos = new ArrayList<>();

    private int categoryId = 0;

    private Unbinder unbinder;

    private CategoryListContract.Presenter presenter;

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Name name = Parcels.unwrap(getArguments().getParcelable(CATEGORY));
            categoryId = 0;
            if (name != null) {
                categoryId = name.getCategoryId();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(true);
        toolsLayout.setVisibility(GONE);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), (int) dpWidth / 120);

        recyclerAllApps.setLayoutManager(gridLayoutManager);

        recyclerAllApps.setHasFixedSize(true);

        presenter.getCategoryAndroidTools(categoryId);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(CategoryListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onInstallSuccessUIUpdate() {
        version.setInstalled(true);
        adapterAllApps.notifyDataSetChanged();
    }

    @Override
    protected void onInstallFailureUIUpdate() {
    }

    @Override
    public void onInstallButtonClick(Version version, View installButton) {
        installApplication(version);
    }

    @Override
    public void onUpdateButtonClick(Version version, View updateButton) {
        installApplication(version);
    }

    @Override
    public void onPlayStoreRedirectButtonClick(Version version) {
        playStoreRedirect(version);
    }

    @Override
    public void getVersionsSuccessful(List<Version> versions) {
        androidVersions.clear();
        androidVersions.addAll(versions);
        adapterAllApps = new CategoryListAdapter(this, androidVersions, downloadAndRatings, images, localizedInfos, R.layout.card_tool_all);
        recyclerAllApps.setAdapter(adapterAllApps);

        presenter.getDownloadAndRatingList();
        presenter.getImages();
        presenter.getLocalizedInfo();

        setHasOptionsMenu(true);

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        toolsLayout.setVisibility(VISIBLE);
    }

    @Override
    public void getVersionsFailed() {
    }

    @Override
    public void getDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadAndRatingList) {
        this.downloadAndRatings.addAll(downloadAndRatingList);
        adapterAllApps.notifyDataSetChanged();
    }

    @Override
    public void getDownloadAndRatingListFailed() {
    }

    @Override
    public void getImagesSuccessful(List<Images> images) {
        this.images.addAll(images);
        adapterAllApps.notifyDataSetChanged();
    }

    @Override
    public void getImagesFailed() {

    }

    @Override
    public void getLocalizedInfoSuccessful(List<LocalizedInfo> localizedInfo) {
        this.localizedInfos.addAll(localizedInfo);
        adapterAllApps.notifyDataSetChanged();
    }

    @Override
    public void getLocalizedInfoFailed() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
