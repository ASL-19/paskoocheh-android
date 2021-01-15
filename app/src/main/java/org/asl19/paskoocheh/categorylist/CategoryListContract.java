package org.asl19.paskoocheh.categorylist;


import android.content.Context;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public interface CategoryListContract {

    interface CategoryListView extends BaseView<Presenter> {
        void getVersionsSuccessful(List<Version> versions);

        void getVersionsFailed();

        void getDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadAndRatingList);

        void getDownloadAndRatingListFailed();

        void getImagesSuccessful(List<Images> images);

        void getImagesFailed();

        void getLocalizedInfoSuccessful(List<LocalizedInfo> localizedInfo);

        void getLocalizedInfoFailed();
    }

    interface CategoryListAdapter {
        Context getContext();
    }

    interface Presenter extends BasePresenter {
        void getCategoryAndroidTools(Integer categoryId);

        void getDownloadAndRatingList();

        void getImages();

        void getLocalizedInfo();
    }
}
