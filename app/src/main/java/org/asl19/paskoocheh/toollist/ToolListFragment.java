package org.asl19.paskoocheh.toollist;


import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;

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

public class ToolListFragment extends Fragment implements ToolListContract.ToolListView, ToolListContract.ToolListAdapter {

    public static final String TAG = ToolListFragment.class.getCanonicalName();
    private static final String RATING = "RATING";
    private static final String DOWNLOAD = "DOWNLOAD";

    @BindView(R.id.my_recycler_featured)
    RecyclerView recyclerFeatured;

    @BindView(R.id.my_recycler_all_apps)
    RecyclerView recyclerAllApps;

    @BindView(R.id.all_apps)
    LinearLayout allAppsLinearLayout;

    @BindView(R.id.featured_relative)
    RelativeLayout featureRelativeLayout;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.content)
    CoordinatorLayout coordinatorLayout;

    @Inject
    DynamoDBMapper dynamoDBMapper;

    private ToolListAdapter adapterFeatured;
    private ToolListAdapter adapterAllApps;

    private List<AndroidTool> androidTools = new ArrayList<>();
    private List<AndroidTool> featuredTools = new ArrayList<>();
    private List<DownloadCount> downloadCountList = new ArrayList<>();
    private List<Rating> ratingList = new ArrayList<>();

    private Unbinder unbinder;

    private ToolListContract.Presenter presenter;

    public static ToolListFragment newInstance() {
        return new ToolListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);

        ((PaskoochehApplication) getContext().getApplicationContext()).getAmazonComponenet().inject(this);

        if (savedInstanceState != null) {
            ratingList = Parcels.unwrap(savedInstanceState.getParcelable(RATING));
            downloadCountList = Parcels.unwrap(savedInstanceState.getParcelable(DOWNLOAD));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        coordinatorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(true);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        RecyclerView.LayoutManager linearLayoutManagerFeatured
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), (int) dpWidth / 175);

        recyclerFeatured.setLayoutManager(linearLayoutManagerFeatured);
        recyclerAllApps.setLayoutManager(gridLayoutManager);

        allAppsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAllApps.smoothScrollToPosition(0);
            }
        });

        recyclerFeatured.setHasFixedSize(true);
        recyclerAllApps.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getFeaturedTools();
        presenter.getAndroidTools();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tool_list_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchPlate = (EditText) searchView.findViewById(searchPlateId);
        searchPlate.setHint("");
        searchPlate.setTextColor(Color.WHITE);

        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_button",null, null);
        ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.ic_search);

        int closeButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(closeButtonId);
        closeButton.setImageResource(R.drawable.ic_close);

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    featureRelativeLayout.setVisibility(View.VISIBLE);
                    adapterAllApps.getFilter().filter(newText);
                } else {
                    featureRelativeLayout.setVisibility(View.GONE);
                    adapterAllApps.getFilter().filter(newText);
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RATING, Parcels.wrap(ratingList));
        outState.putParcelable(DOWNLOAD, Parcels.wrap(downloadCountList));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(ToolListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getToolsSuccessful(List<AndroidTool> tools) {
        androidTools.clear();
        androidTools.addAll(tools);
        adapterAllApps = new ToolListAdapter(this, androidTools, downloadCountList, ratingList, R.layout.card_tool_all);
        recyclerAllApps.setAdapter(adapterAllApps);

        setHasOptionsMenu(true);

        if (ratingList.isEmpty()) {
            presenter.getRatingList();
        }

        if (downloadCountList.isEmpty()) {
            presenter.getDownloadCountList();
        }
    }

    @Override
    public void getToolsFailed() {
    }

    @Override
    public void getFeaturedSuccessful(List<AndroidTool> tools) {
        featuredTools.clear();
        featuredTools.addAll(tools);
        adapterFeatured = new ToolListAdapter(this, featuredTools, downloadCountList, ratingList, R.layout.card_tool_featured);
        recyclerFeatured.setAdapter(adapterFeatured);

        coordinatorLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void getFeaturedFailed() {
    }

    @Override
    public void getDownloadCountListSuccessful(DownloadCountList downloadCountList) {
        this.downloadCountList.addAll(downloadCountList.getApps());
        adapterFeatured.notifyDataSetChanged();
        adapterAllApps.notifyDataSetChanged();
    }

    @Override
    public void getDownloadCountListFailed() {
    }

    @Override
    public void getRatingListSuccessful(RatingList ratingList) {
        this.ratingList.addAll(ratingList.getItems());
        adapterFeatured.notifyDataSetChanged();
        adapterAllApps.notifyDataSetChanged();
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
    public void onPermissionRequested(Integer code) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
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
