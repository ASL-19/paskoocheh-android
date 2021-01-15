package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Entity
@Parcel
@Data
public class Review {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("platform_name")
    @Expose
    public String platformName;
    @SerializedName("tool_id")
    @Expose
    public Integer toolId;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("rating")
    @Expose
    public Double rating;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("tool_version")
    @Expose
    public String toolVersion;
    @SerializedName("tool_name")
    @Expose
    public String toolName;
    @SerializedName("subject")
    @Expose
    public String subject;

    public Review() {}
}
