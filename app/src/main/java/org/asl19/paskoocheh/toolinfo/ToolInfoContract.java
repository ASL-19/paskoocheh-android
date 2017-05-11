package org.asl19.paskoocheh.toolinfo;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

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

        void onRegisterDownloadSuccessful();

        void onRegisterDownloadFailed();
    }

    interface Presenter extends BasePresenter {

        void getTool(long toolId);

        void getRating(String toolName);

        void getDownloadCount(String toolName);

        void getToolReviews(GetReviewRequest getReviewRequest);

        void registerDownload(String uuid, String tool, DynamoDBMapper dynamoDBMapper);
    }
}


