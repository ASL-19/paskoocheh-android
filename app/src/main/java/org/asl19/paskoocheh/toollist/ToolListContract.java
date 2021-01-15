package org.asl19.paskoocheh.toollist;


import android.content.Context;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public interface ToolListContract {

    interface ToolListView extends BaseView<Presenter> {
        void getCategoryVersionsSuccessful(List<Version> versions, Name categoryName);

        void getCategoryVersionsFailed();

        void getSetVersionsSuccessful(List<Version> versions, String type);

        void getSetVersionsFailed();

        void getDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadAndRatingList);

        void getDownloadAndRatingListFailed();

        void getImagesSuccessful(List<Images> images);

        void getImagesFailed();

        void getLocalizedInfoSuccessful(List<LocalizedInfo> localizedInfo);

        void getLocalizedInfoFailed();

        void onGetCategoryNamesSuccessful(List<Name> names);

        void onGetCategoryNamesFailed();
    }

    interface ToolListAdapter {
        Context getContext();
    }

    interface Presenter extends BasePresenter {
        void getCategoryAndroidTools(Name category);

        void getDownloadAndRatingList();

        void getImages();

        void getLocalizedInfo();

        void getCategoryNames();

        void getFeatured();

        void getTopDownloads();

        void getUpdated();
    }
}
