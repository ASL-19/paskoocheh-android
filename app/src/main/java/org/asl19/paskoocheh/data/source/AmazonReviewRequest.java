package org.asl19.paskoocheh.data.source;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.asl19.paskoocheh.data.AmazonContentBodyRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AmazonReviewRequest extends AmazonContentBodyRequest {

    @SerializedName("tool")
    private String tool;

    @SerializedName("tool_version")
    private String toolVersion;

    @SerializedName("title")
    private String title;

    @SerializedName("text")
    private String text;

    @SerializedName("rating")
    private String rating;

    public AmazonReviewRequest(Context context) {
        super(context);
    }
}
