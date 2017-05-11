package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

/**
 * Tool Rating Gson Pojo.
 */
@Parcel
@Data
public class Rating {

    @SerializedName("count")
    @Expose
    String count;
    @SerializedName("platform")
    @Expose
    String platform;
    @SerializedName("app_name")
    @Expose
    String appName;
    @SerializedName("rating")
    @Expose
    String rating;

    public Rating() {}
}