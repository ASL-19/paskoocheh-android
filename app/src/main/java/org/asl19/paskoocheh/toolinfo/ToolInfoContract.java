package org.asl19.paskoocheh.toolinfo;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.data.GetReviewRequest;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.ReviewList;

public interface ToolInfoContract {

    interface ToolInfoView extends BaseView<Presenter> {

        void onGetToolSuccessful(AndroidTool tool);

        void onGetToolFailed();

        void onGetRatingSuccessful(String rating);

        void onGetRatingFailed();

        void onGetDownloadCountSuccessful(String count);

        void onGetDownlaodCountFailed();

        void onGetReviewListSuccessful(ReviewList reviewList);

        void onGetReviewListFailed();

        void onRegisterInstallSuccessful();

        void onRegisterInstallFailed();
    }

    interface Presenter extends BasePresenter {

        void getTool(long toolId);

        void getRating(String toolName);

        void getDownloadCount(String toolName);

        void getToolReviews(GetReviewRequest getReviewRequest);

        void registerInstall(String uuid, String tool);
    }
}


