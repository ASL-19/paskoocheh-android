package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

/**
 * DownloadCount Gson Pojo.
 */
@Parcel
@Data
public class DownloadCount {

    @SerializedName("platform")
    @Expose
    String platform;
    @SerializedName("download_count")
    @Expose
    Integer downloadCount;
    @SerializedName("app_name")
    @Expose
    String appName;

    public DownloadCount() {}
}