package org.asl19.paskoocheh.data;


import com.google.gson.annotations.SerializedName;

import org.asl19.paskoocheh.Constants;

import lombok.Data;

@Data
public class SendReviewRequest {

    @SerializedName("id")
    public String id;

    @SerializedName("app_name")
    public String appName;

    @SerializedName("platform")
    public String platform = Constants.ANDROID;

    @SerializedName("version")
    public String version;

    @SerializedName("title")
    public String title;

    @SerializedName("text")
    public String text;

    @SerializedName("rating")
    public Float rating;

    @SerializedName("cognito")
    public String cognito;

    public SendReviewRequest() {
    }
}
