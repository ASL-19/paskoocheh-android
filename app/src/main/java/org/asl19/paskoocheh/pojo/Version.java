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
@Entity
public class Version {

    @SerializedName("tool_id")
    @Expose
    public Integer toolId;

    @SerializedName("version_number")
    @Expose
    public String versionNumber;

    @SerializedName("download_via")
    @Expose
    @Embedded
    public DownloadVia downloadVia;

    @SerializedName("release_url")
    @Expose
    public String releaseUrl;

    @SerializedName("s3_key")
    @Expose
    public String s3Key;

    @SerializedName("s3_bucket")
    @Expose
    public String s3Bucket;

    @SerializedName("checksum")
    @Expose
    public String checksum;

    @SerializedName("release_date")
    @Expose
    public String releaseDate;

    @SerializedName("release_jdate")
    @Expose
    public String releaseJDate;

    @SerializedName("version_code")
    @Expose
    public Integer versionCode;

    @SerializedName("package_name")
    @Expose
    public String packageName;

    @SerializedName("os_id")
    @Expose
    public String osId;

    @SerializedName("faq_urls")
    @Expose
    public String faqUrls;

    @SerializedName("last_modified")
    @Expose
    public String lastModified;

    @SerializedName("images")
    @Expose
    @Ignore
    public Images images;

    @SerializedName("permissions")
    @Expose
    public String permissions;

    @SerializedName("size")
    @Expose
    public Integer size;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("categories")
    @Expose
    public List<String> categories = new ArrayList<>();

    @SerializedName("app_name")
    @Expose
    public String appName;

    @SerializedName("version_codes")
    @Expose
    public List<AppDownloadInfoForVersionCode> appDownloadInfoForVersionCodes = new ArrayList<>();

    @SerializedName("devices")
    @Expose
    public List<DeviceInfo> devices = new ArrayList<>();

    @SerializedName("is_installable")
    @Expose
    public boolean isInstallable = true;

    @Ignore
    public boolean installed;

    @Ignore
    public boolean updateAvailable;

    public Version() {}

    public void setCurrentAppDownloadInfo(AppDownloadInfoForVersionCode appDownloadInfoForVersionCode) {
        this.versionCode = appDownloadInfoForVersionCode.versionCode;
        this.downloadVia = appDownloadInfoForVersionCode.downloadVia;
        this.s3Bucket = appDownloadInfoForVersionCode.s3Bucket;
        this.s3Key = appDownloadInfoForVersionCode.s3Key;
        this.checksum = appDownloadInfoForVersionCode.checksum;
        this.size = appDownloadInfoForVersionCode.size;
    }
}