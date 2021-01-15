package org.asl19.paskoocheh.pojo;

import androidx.room.Entity;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Data
@Entity(primaryKeys = {"toolId", "platformName"})
@Parcel
public class DownloadAndRating {
    @SerializedName("tool_id")
    @Expose
    @NonNull
    public Integer toolId = 0;
    @SerializedName("rating")
    @Expose
    public Double rating;
    @SerializedName("platform_name")
    @Expose
    @NonNull
    public String platformName = "";
    @SerializedName("rating_count")
    @Expose
    public Integer ratingCount;
    @SerializedName("download_count")
    @Expose
    public Integer downloadCount;
    @SerializedName("tool_name")
    @Expose
    public String toolName;

    public DownloadAndRating() {}
}
