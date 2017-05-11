package org.asl19.paskoocheh.service;

import org.asl19.paskoocheh.data.GetReviewRequest;
import org.asl19.paskoocheh.data.RegisterDownloadRequest;
import org.asl19.paskoocheh.data.SendReviewRequest;
import org.asl19.paskoocheh.pojo.DownloadCountList;
import org.asl19.paskoocheh.pojo.RatingList;
import org.asl19.paskoocheh.pojo.ReviewList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import static org.asl19.paskoocheh.Constants.DOWNLOAD;
import static org.asl19.paskoocheh.Constants.RATING;

/**
 * Retrofit Paskoocheh API Interface.
 */
public interface PaskoochehApiService {

    /**
     * Send review
     *
     * @param sendReviewRequest containing uuid, app name, platform, version, title, text, and rating.
     * @return Call object containing response.
     */
    @PUT(RATING)
    Call<ResponseBody> sendReview(@Body SendReviewRequest sendReviewRequest);

    /**
     * Retrieve reviews for the given tool.
     *
     * @param getReviewRequest containing package name, app name, and platorm.
     * @return Call object containing ReviewList response.
     */
    @POST(RATING)
    Call<ReviewList> getToolReviewList(@Body GetReviewRequest getReviewRequest);

    /**
     * Get the average rating for all applications.
     *
     * @return Call object containing RatingList which contains average rating of all apps.
     */
    @GET(RATING)
    Call<RatingList> getRatingList();

    /**
     * Register download of app.
     *
     * @param downloadRequest containing app name, platform, id, and version.
     * @return Call object containing JSONObject response.
     */
    @PUT(DOWNLOAD)
    Call<ResponseBody> registerDownload(@Body RegisterDownloadRequest downloadRequest);

    /**
     * Retrieve number of downloads of all apps.
     *
     * @return Call object containing DownloadCountList which contains number of downloads of all apps.
     */
    @GET(DOWNLOAD)
    Call<DownloadCountList> getDownloadCountList();
}