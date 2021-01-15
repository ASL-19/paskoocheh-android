package org.asl19.paskoocheh.search;


import android.content.Context;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Version;

import java.util.HashSet;
import java.util.List;

public interface SearchContract {

    interface SearchView extends BaseView<Presenter> {
        void getVersionsSuccessful(List<Version> versions);

        void getVersionsFailed();

        void getDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadAndRatingList);

        void getDownloadAndRatingListFailed();

        void getImagesSuccessful(List<Images> images);

        void getImagesFailed();

        void getLocalizedInfoSuccessful(List<LocalizedInfo> localizedInfo);

        void getLocalizedInfoFailed();

        void onGetCategoryNamesSuccessful(List<Name> names);

        void onGetCategoryNamesFailed();
    }

    interface SearchAdapter {
        Context getContext();
    }

    interface Presenter extends BasePresenter {
        void getAndroidTools();

        void getSearchTools(HashSet<Integer> searchTerm, HashSet<String> category, String query);

        void getDownloadAndRatingList();

        void getImages();

        void getLocalizedInfo();

        void getCategoryNames();
    }
}
