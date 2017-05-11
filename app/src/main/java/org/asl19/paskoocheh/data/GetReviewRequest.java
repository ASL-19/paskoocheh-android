package org.asl19.paskoocheh.data;

import com.google.gson.annotations.SerializedName;

import org.asl19.paskoocheh.Constants;

import lombok.Data;

@Data
public class GetReviewRequest {

    @SerializedName("pkg_name")
    private String pkgName;

    @SerializedName("app_name")
    private String appName;

    @SerializedName("platform")
    private String platform = Constants.ANDROID;

    @SerializedName("cognito")
    private String cognito;

    public GetReviewRequest() {
    }
}
