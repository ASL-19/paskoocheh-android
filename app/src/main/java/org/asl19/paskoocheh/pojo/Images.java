package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Parcel
@Data
@Entity(primaryKeys = {"toolId", "versionId"})
public class Images {
    public int toolId = 0;

    public int versionId = 0;

    @SerializedName("logo")
    @Expose
    public List<Image> logo = new ArrayList<>();

    @SerializedName("screenshot")
    @Expose
    public List<Image> screenshot = new ArrayList<>();

    public Images() {}
}