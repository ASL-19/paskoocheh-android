package org.asl19.paskoocheh.service;

import org.asl19.paskoocheh.pojo.DownloadCountList;
import org.asl19.paskoocheh.pojo.RatingList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static org.asl19.paskoocheh.Constants.DOWNLOAD;
import static org.asl19.paskoocheh.Constants.RATING;

/**
 * Retrofit Paskoocheh API Interface.
 */
public interface PaskoochehApiService {

    /**
     * Get the average rating for all applications.
     *
     * @return Call object containing RatingList which contains average rating of all apps.
     */
    @GET(RATING)
    Call<RatingList> getRatingList();

    /**
     * Retrieve number of downloads of all apps.
     *
     * @return Call object containing DownloadCountList which contains number of downloads of all apps.
     */
    @GET(DOWNLOAD)
    Call<DownloadCountList> getDownloadCountList();

    /**
     * Submit Paskoocheh Feedback
     *
     * @param file
     * @return
     */
    @Multipart
    @POST("/")
    Call<ResponseBody> submitAmazonRequest(
            @Part("acl") RequestBody acl,
            @Part("key") RequestBody key,
            @Part("policy") RequestBody policy,
            @Part("x-amz-algorithm") RequestBody algorithm,
            @Part("x-amz-credential") RequestBody credential,
            @Part("x-amz-date") RequestBody date,
            @Part("x-amz-signature") RequestBody signature,
            @Part("file") RequestBody file
    );
}