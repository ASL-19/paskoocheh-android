package org.asl19.paskoocheh.pojo;


import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Parcel
@Data
public class AppDownloadInfoForVersionCode {
    @SerializedName("version_code")
    @Expose
    public Integer versionCode;

    @SerializedName("download_via")
    @Expose
    @Embedded
    public DownloadVia downloadVia;

    @SerializedName("s3_bucket")
    @Expose
    public String s3Bucket;

    @SerializedName("s3_key")
    @Expose
    public String s3Key;

    @SerializedName("checksum")
    @Expose
    public String checksum;

    @SerializedName("size")
    @Expose
    public Integer size;

    @SerializedName("signature_file")
    @Expose
    public String signatureFile;

    public AppDownloadInfoForVersionCode() {}
}
