package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
public class Image {
    @SerializedName("url")
    @Expose
    public String url = "";

    @SerializedName("full_bleed")
    @Expose
    public boolean fullBleed = false;

    public Image() {}
}
