package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

/**
 * Review Gson Pojo.
 */
@Parcel
@Data
public class Review {

    @SerializedName("rating")
    @Expose
    Double rating;
    @SerializedName("date_other")
    @Expose
    DateOther dateOther;
    @SerializedName("text")
    @Expose
    String text;
    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("version")
    @Expose
    String version;
    @SerializedName("date")
    @Expose
    Double date;

    public Review() {}
}
