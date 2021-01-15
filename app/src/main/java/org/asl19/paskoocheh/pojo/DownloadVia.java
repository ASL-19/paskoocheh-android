package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
public class DownloadVia {
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("s3")
    @Expose
    public String s3;
    @SerializedName("url")
    @Expose
    public String url;

    public DownloadVia() {}
}
