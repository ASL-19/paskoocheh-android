package org.asl19.paskoocheh.search;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Version;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.inflate;

public class SearchFragment extends Fragment implements SearchContract.SearchView, SearchContract.SearchAdapter {

    public static final String TAG = SearchFragment.class.getCanonicalName();
    private static final String SEARCH = "SEARCH";

    @BindView(R.id.my_recycler_all_apps)
    RecyclerView recyclerAllApps;

    @BindView(R.id.list_header)
    LinearLayout headerLayout;

    @BindView(R.id.header_text)
    TextView headerText;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tools_layout)
    LinearLayout toolsLayout;

    @BindView(R.id.category_layout)
    LinearLayout categoryLayout;

    @BindView(R.id.categories)
    FlexboxLayout categories;

    private SearchAdapter adapterAllApps;
    private SearchView.OnQueryTextListener queryTextListener;
    private SearchView searchView;

    private List<Version> androidVersions = new ArrayList<>();
    private List<DownloadAndRating> downloadAndRatings = new ArrayList<>();
    private List<Images> images = new ArrayList<>();
    private List<LocalizedInfo> localizedInfos = new ArrayList<>();
    private List<Name> categoryNames = new ArrayList<>();
    private String search = "";

    private Unbinder unbinder;

    private SearchContract.Presenter presenter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            search = savedInstanceState.getString(SEARCH);
        }

        toolsLayout.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        presenter.getLocalizedInfo();
        presenter.getCategoryNames();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), (int) dpWidth / 120);

        recyclerAllApps.setLayoutManager(gridLayoutManager);

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAllApps.smoothScrollToPosition(0);
            }
        });

        recyclerAllApps.setHasFixedSize(true);

        adapterAllApps = new SearchAdapter(this, androidVersions, downloadAndRatings, images, localizedInfos, R.layout.card_tool_all);
        recyclerAllApps.setAdapter(adapterAllApps);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tool_list_search, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchPlate = searchView.findViewById(searchPlateId);
        searchPlate.setHint("");
        searchPlate.setTextColor(Color.WHITE);

        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_button",null, null);
        ImageView searchIcon = searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.ic_search);

        int closeButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = searchView.findViewById(closeButtonId);
        closeButton.setImageResource(R.drawable.ic_close);

        queryTextListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    headerText.setText("");
                    search = "";
                    categoryLayout.setVisibility(View.VISIBLE);
                    toolsLayout.setVisibility(View.GONE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                headerText.setText(String.format("نتیجه جستجو برای \"%s\"", query));
                search = query;
                query = query.toLowerCase();

                HashSet<String> categoryIds = new HashSet<>();
                for (Name categoryName: categoryNames) {
                    if (categoryName.getFa().toLowerCase().contains(query) || categoryName.getEn().toLowerCase().contains(query)) {
                        categoryIds.add(String.valueOf(categoryName.getCategoryId()));
                    }
                }

                HashSet<Integer> hashSet = new HashSet<>();
                for (LocalizedInfo localizedInfo: localizedInfos) {
                    if (localizedInfo.getName().toLowerCase().contains(query) || localizedInfo.description.toLowerCase().contains(query) || localizedInfo.company.toLowerCase().contains(query)) {
                        hashSet.add(localizedInfo.getToolId());
                    }
                }
                presenter.getSearchTools(hashSet, categoryIds, query);

                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshLayout.setEnabled(true);
                searchView.clearFocus();
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setIconified(false);

        if (!search.isEmpty()) {
            searchView.setQuery(search, true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(searchView != null) {
            outState.putString(SEARCH, searchView.getQuery().toString());
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getVersionsSuccessful(List<Version> versions) {
        androidVersions.clear();
        androidVersions.addAll(versions);
        adapterAllApps.notifyDataSetChanged();

        presenter.getDownloadAndRatingList();
        presenter.getImages();

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        toolsLayout.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.GONE);
    }

    @Override
    public void getVersionsFailed() {
        androidVersions.clear();
        adapterAllApps.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        toolsLayout.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.GONE);
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
    public void onGetCategoryNamesSuccessful(List<Name> names) {
        this.categoryNames = names;
        for (final Name categoryName: categoryNames) {
            final Button text = (Button) inflate(getContext(), R.layout.button_search_category, null);
            text.setAllCaps(false);
            text.setText(categoryName.getFa());
            if (categoryName.getFa().isEmpty()) {
                text.setText(categoryName.getEn());
            }

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (categoryName.getFa().isEmpty()) {
                        searchView.setQuery(categoryName.getEn(), true);
                    } else {
                        searchView.setQuery(categoryName.getFa(), true);
                    }
                }
            });
            categories.addView(text);
        }
        categoryLayout.setVisibility(View.VISIBLE);
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
