package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * ReviewList Gson Pojo.
 */
@Parcel
@Data
public class ReviewList {

    @SerializedName("count")
    @Expose
    Integer count;
    @SerializedName("rating")
    @Expose
    Double rating;
    @SerializedName("reviews")
    @Expose
    List<Review> allReviews = new ArrayList<>();
    @SerializedName("app_name")
    @Expose
    String appName;
    @SerializedName("platform")
    @Expose
    String platform;

    public ReviewList() {}
}