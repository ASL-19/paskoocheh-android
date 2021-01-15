package org.asl19.paskoocheh.toolinfo;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Faq;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Tutorial;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public interface ToolInfoContract {

    interface ToolInfoView extends BaseView<Presenter> {

        void onGetVersionSuccessful(Version version);

        void onGetVersionFailed();

        void onGetDownloadAndRatingSuccessfull(DownloadAndRating downloadAndRatingList);

        void onGetDownloadAndRatingFailed();

        void onGetReviewListSuccessful(List<Review> reviewList);

        void onGetReviewListFailed();

        void onGetFaqListSuccessful(List<Faq> faqList);

        void onGetFaqListFailed();

        void onGetLocalizedInfoSuccessful(LocalizedInfo localizedInfo);

        void onGetLocalizedInfoFailed();

        void onGetGuideListSuccessful(List<Guide> guides);

        void onGetGuideListFailed();

        void onGetToolSuccessful(Tool tool);

        void onGetToolFailed();

        void onGetCategoryNamesSuccessful(List<Name> names);

        void onGetCategoryNamesFailed();

        void onGetVersionImagesSuccessful(Images images);

        void onGetVersionImagesFailed();

        void onGetToolImagesSuccessful(Images images);

        void onGetToolImagesFailed();

        void onGetTutorialsSuccessful(List<Tutorial> tutorials);

        void onGetTutorialsFailed();
    }

    interface Presenter extends BasePresenter {

        void getVersion(long versionId);

        void getDownloadAndRating(long toolId);

        void getToolReviews(int toolId);

        void getFaqList(int toolId);

        void getLocalizedInfo(int toolId);

        void getGuideList(int toolId);

        void getTool(int toolId);

        void getCategoryNames();

        void getVersionImages(int versionId);

        void getToolImages(int toolId);

        void getTutorials(int toolId);
    }
}


