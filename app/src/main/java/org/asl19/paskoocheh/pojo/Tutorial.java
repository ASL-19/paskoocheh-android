package org.asl19.paskoocheh.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
@Entity
public class Tutorial {

    public int toolId;

    @SerializedName("video_link")
    @Expose
    public String videoLink;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("order")
    @Expose
    public Integer order;
    @SerializedName("last_modified")
    @Expose
    public String lastModified;
    @SerializedName("video")
    @Expose
    public String video;
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("language")
    @Expose
    public String language;

    public Tutorial() {}

}