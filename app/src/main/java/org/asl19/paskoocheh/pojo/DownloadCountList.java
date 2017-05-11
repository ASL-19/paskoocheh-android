package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * DownloadCountList Gson Pojo.
 */
@Parcel
@Data
public class DownloadCountList {

    @SerializedName("count")
    @Expose
    Integer count;
    @SerializedName("items")
    @Expose
    List<DownloadCount> apps = new ArrayList<>();

    public DownloadCountList() {}
}