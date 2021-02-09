package org.asl19.paskoocheh.toollist;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.categorylist.CategoryListActivity;
import org.asl19.paskoocheh.categorylist.CategoryListFragment;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.search.SearchActivity;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.inflate;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.CATEGORY;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.FEATURED;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.TOP_DOWNLOADS;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.TYPE;

public class ToolListFragment extends Fragment implements ToolListContract.ToolListView, ToolListContract.ToolListAdapter {

    public static final String TAG = ToolListFragment.class.getCanonicalName();

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tools_layout)
    LinearLayout toolsLayout;

    @BindView(R.id.tool_list_layout)
    LinearLayout toolListLayout;

    @BindView(R.id.category_recycler)
    RecyclerView categoryRecycler;

    private List<DownloadAndRating> downloadAndRatings = new ArrayList<>();
    private List<Images> images = new ArrayList<>();
    private List<LocalizedInfo> localizedInfos = new ArrayList<>();
    private List<ToolListAdapter> adapterList = new ArrayList<>();

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolListLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(true);

        presenter.getFeatured();

        presenter.getDownloadAndRatingList();
        presenter.getImages();
        presenter.getLocalizedInfo();
        presenter.getCategoryNames();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tool_list_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_button",null, null);
        ImageView searchIcon = searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.ic_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        for (ToolListAdapter adapter: adapterList) {
            adapter.notifyDataSetChanged();
        }
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
    public void getCategoryVersionsSuccessful(List<Version> versions, final Name categoryName) {
        RecyclerView.LayoutManager layoutManager
                = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        RelativeLayout relativeLayout = (RelativeLayout) inflate(getContext(), R.layout.row_tool_list_recyclerview, null);
        RecyclerView recyclerView = relativeLayout.findViewById(R.id.my_recycler_featured);

        LinearLayout allAppsLayout = relativeLayout.findViewById(R.id.all_apps);
        allAppsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryListActivity.class);
                intent.putExtra(CategoryListFragment.CATEGORY, Parcels.wrap(categoryName));
                intent.putExtra(TYPE, CATEGORY);
                getContext().startActivity(intent);
            }
        });

        TextView textView = relativeLayout.findViewById(R.id.category);
        if (!categoryName.getFa().isEmpty()) {
            textView.setText(categoryName.getFa());
        } else {
            textView.setText(categoryName.getEn());
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        ToolListAdapter adapterAllApps = new ToolListAdapter(this, versions, downloadAndRatings, images, localizedInfos, R.layout.card_tool_featured);
        adapterList.add(adapterAllApps);
        recyclerView.setAdapter(adapterAllApps);
        toolsLayout.addView(relativeLayout);
    }

    @Override
    public void getCategoryVersionsFailed() {
    }

    @Override
    public void getSetVersionsSuccessful(List<Version> versions, final String type) {
        switch (type) {
            case FEATURED:
                presenter.getTopDownloads();
                break;
            case TOP_DOWNLOADS:
                presenter.getUpdated();
                break;
            default:
                break;
        }

        if (versions.size() > 0) {

            RecyclerView.LayoutManager layoutManager
                    = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
            RelativeLayout relativeLayout = (RelativeLayout) inflate(getContext(), R.layout.row_tool_list_recyclerview, null);
            RecyclerView recyclerView = relativeLayout.findViewById(R.id.my_recycler_featured);

            LinearLayout allAppsLayout = relativeLayout.findViewById(R.id.all_apps);
            allAppsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), CategoryListActivity.class);
                    intent.putExtra(TYPE, type);
                    getContext().startActivity(intent);
                }
            });

            TextView textView = relativeLayout.findViewById(R.id.category);
            int position = 0;
            switch (type) {
                case CategoryListActivity.FEATURED:
                    textView.setText(getString(R.string.featured));
                    position = 0;
                    break;
                case CategoryListActivity.TOP_DOWNLOADS:
                    textView.setText(getString(R.string.most_downloads));
                    position = 1;
                    break;
                case CategoryListActivity.UPDATED:
                    textView.setText(getString(R.string.updated));
                    position = 2;
                    break;

                default:
                    textView.setText("");
                    break;
            }

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);
            ToolListAdapter adapterAllApps = new ToolListAdapter(this, versions, downloadAndRatings, images, localizedInfos, R.layout.card_tool_featured);
            adapterList.add(adapterAllApps);
            recyclerView.setAdapter(adapterAllApps);
            toolsLayout.addView(relativeLayout, position);
        }
    }

    @Override
    public void getSetVersionsFailed() {
    }

    @Override
    public void getDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadAndRatingList) {
        this.downloadAndRatings.addAll(downloadAndRatingList);
        for (ToolListAdapter adapter: adapterList) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getDownloadAndRatingListFailed() {
    }

    @Override
    public void getImagesSuccessful(List<Images> images) {
        this.images.addAll(images);
        for (ToolListAdapter adapter: adapterList) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getImagesFailed() {

    }

    @Override
    public void getLocalizedInfoSuccessful(List<LocalizedInfo> localizedInfo) {
        this.localizedInfos.addAll(localizedInfo);
        for (ToolListAdapter adapter: adapterList) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getLocalizedInfoFailed() {

    }

    @Override
    public void onGetCategoryNamesSuccessful(List<Name> names) {
        Collections.reverse(names);

        for (Name name: names) {
            presenter.getCategoryAndroidTools(name);
        }

        RecyclerView.LayoutManager linearLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        categoryRecycler.setLayoutManager(linearLayoutManager);
        categoryRecycler.setHasFixedSize(false);
        ToolListCategoryAdapter categoryAdapter = new ToolListCategoryAdapter(names, getContext(), R.layout.card_categories);
        categoryRecycler.setAdapter(categoryAdapter);

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        toolListLayout.setVisibility(View.VISIBLE);

        setHasOptionsMenu(true);
    }

    @Override
    public void onGetCategoryNamesFailed() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
