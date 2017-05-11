package org.asl19.paskoocheh.data.source;


import org.asl19.paskoocheh.pojo.RatingList;

public interface RatingDataSource {

    interface GetRatingListCallback {

        void onGetRatingSuccessful(RatingList ratingList);

        void onGetRatingsFailed();
    }

    void getRatingList(RatingDataSource.GetRatingListCallback callback);

    interface GetRatingCallback {

        void onGetRatingSuccessful(String rating);

        void onGetRatingFailed();
    }

    void getRating(String toolName, RatingDataSource.GetRatingCallback callback);
}