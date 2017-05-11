package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Rating Summary Gson Pojo.
 */
@Parcel
@Data
public class RatingList {

    @SerializedName("count")
    @Expose
    Integer count;
    @SerializedName("items")
    @Expose
    List<Rating> items = new ArrayList<>();

    public RatingList() {}
}