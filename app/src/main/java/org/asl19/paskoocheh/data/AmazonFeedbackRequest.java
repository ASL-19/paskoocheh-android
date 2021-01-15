package org.asl19.paskoocheh.data;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class AmazonFeedbackRequest extends AmazonContentBodyRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("text")
    private String text;

    @SerializedName("user_id")
    private String userId;

    public AmazonFeedbackRequest(Context context) {
        super(context);
    }
}
