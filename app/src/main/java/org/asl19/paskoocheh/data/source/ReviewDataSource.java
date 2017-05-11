package org.asl19.paskoocheh.data.source;


import org.asl19.paskoocheh.data.GetReviewRequest;
import org.asl19.paskoocheh.data.SendReviewRequest;
import org.asl19.paskoocheh.pojo.ReviewList;

public interface ReviewDataSource {

    interface GetReviewListCallback {

        void onGetReviewsSuccessful(ReviewList reviewList);

        void onGetReviewsFailed();
    }

    void getReviewList(GetReviewRequest getReviewRequest, ReviewDataSource.GetReviewListCallback callback);

    interface SendReviewCallback {

        void onSendReviewSuccessful();

        void onSendReviewFailed();
    }

    void submitReview(SendReviewRequest sendReviewRequest, ReviewDataSource.SendReviewCallback callback);
}