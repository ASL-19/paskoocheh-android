package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.DownloadAndRating;

import java.util.List;

public interface DownloadAndRatingDataSource {

    interface GetDownloadAndRatingListCallback {

        void onGetDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadAndRating);

        void onGetDownloadAndRatingListFailed();
    }

    interface GetToolDownloadAndRatingListCallback {

        void onGetDownloadAndRatingListSuccessful(DownloadAndRating downloadAndRating);

        void onGetDownloadAndRatingListFailed();
    }

    void getToolDownloadAndRatings(long toolId, GetToolDownloadAndRatingListCallback callback);

    void getDownloadAndRatings(GetDownloadAndRatingListCallback callback);

    void getDownloadAndRatingsDownloadCountDesc(GetDownloadAndRatingListCallback callback);

    void saveDownloadAndRating(@NonNull DownloadAndRating... downloadAndRatings);

    void clearTable();
}