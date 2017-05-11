package org.asl19.paskoocheh.data;


import com.google.gson.annotations.SerializedName;

import org.asl19.paskoocheh.Constants;

import lombok.Data;

@Data
public class RegisterDownloadRequest {

    @SerializedName("id")
    public String id;

    @SerializedName("app_name")
    public String appName;

    @SerializedName("platform")
    public String platform = Constants.ANDROID;

    @SerializedName("version")
    public String version;

    @SerializedName("cognito")
    public String cognito;

    public RegisterDownloadRequest() {}
}
