package org.asl19.paskoocheh.installedtoollist;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.DownloadCount;
import org.asl19.paskoocheh.pojo.DownloadCountList;
import org.asl19.paskoocheh.pojo.Rating;
import org.asl19.paskoocheh.pojo.RatingList;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;

public class InstalledToolListFragment extends Fragment implements InstalledToolListContract.ListView,
        InstalledToolListContract.ToolListAdapter {

    public static final String TAG = InstalledToolListFragment.class.getCanonicalName();
    private static final String RATING = "RATING";
    private static final String DOWNLOAD = "DOWNLOAD";

    @BindView(R.id.installed_tools)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    DynamoDBMapper dynamoDBMapper;

    private List<AndroidTool> androidTools = new ArrayList<>();
    private List<DownloadCount> downloadCountList = new ArrayList<>();
    private List<Rating> ratingList = new ArrayList<>();

    private InstalledToolListContract.Presenter presenter;

    private InstalledToolListAdapter adapter;

    private Unbinder unbinder;

    public static InstalledToolListFragment newInstance() {
        return new InstalledToolListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((PaskoochehApplication) getContext().getApplicationContext()).getAmazonComponenet().inject(this);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);

        if (savedInstanceState != null) {
            ratingList = Parcels.unwrap(savedInstanceState.getParcelable(RATING));
            downloadCountList = Parcels.unwrap(savedInstanceState.getParcelable(DOWNLOAD));
        }
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getInstalledTools();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RATING, Parcels.wrap(ratingList));
        outState.putParcelable(DOWNLOAD, Parcels.wrap(downloadCountList));
        super.onSaveInstanceState(outState);
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
    public void onPermissionRequested(Integer code) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
    }

    @Override
    public void onGetInstalledToolListSuccessful(List<AndroidTool> toolList) {
        androidTools.clear();
        androidTools.addAll(toolList);
        adapter = new InstalledToolListAdapter(this, androidTools, downloadCountList, ratingList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        if (ratingList.isEmpty()) {
            presenter.getRatingList();
        }

        if (downloadCountList.isEmpty()) {
            presenter.getDownloadCountList();
        }
    }

    @Override
    public void onGetInstalledToolListFailed() {

    }

    @Override
    public void getDownloadCountListSuccessful(DownloadCountList downloadCountList) {
        this.downloadCountList.addAll(downloadCountList.getApps());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getDownloadCountListFailed() {
    }

    @Override
    public void getRatingListSuccessful(RatingList ratingList) {
        this.ratingList.addAll(ratingList.getItems());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getRatingListFailed() {
    }

    @Override
    public void registerDownload(String tool) {
        String uuid = getContext().getSharedPreferences(
                PASKOOCHEH_PREFS,
                Context.MODE_PRIVATE
        ).getString(PASKOOCHEH_UUID, "");

        presenter.registerDownload(uuid, tool, dynamoDBMapper);
    }

    @Override
    public void onRegisterDownloadSuccessful() {

    }

    @Override
    public void onRegisterDownloadFailed() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
