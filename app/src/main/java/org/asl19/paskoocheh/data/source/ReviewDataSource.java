package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Review;

import java.util.List;

public interface ReviewDataSource {

    interface GetReviewListCallback {

        void onGetReviewsSuccessful(List<Review> reviewList);

        void onGetReviewsFailed();
    }

    void getReviewList(int toolId, ReviewDataSource.GetReviewListCallback callback);

    void saveReview(@NonNull final Review... reviews);

    void clearTable();
}