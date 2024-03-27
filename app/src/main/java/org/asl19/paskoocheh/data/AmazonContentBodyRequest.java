package org.asl19.paskoocheh.data;


import android.content.Context;
import android.os.Build;

import com.google.gson.annotations.SerializedName;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import lombok.Data;

import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;

@Data
public class AmazonContentBodyRequest {
    public static final String FEEDBACK = "feedback";
    public static final String DOWNLOAD = "download";
    public static final String FAILED = "failed";
    public static final String INSTALL = "install";
    public static final String UPDATE = "update";
    public static final String RATING = "rating";
    public static final String REPORT_DEVICE_APP_NOT_INSTALLABLE = "report_device_app_not_installable";

    @SerializedName("type")
    private String type;

    @SerializedName("user_uuid")
    private String userUUID;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("channel")
    private String channel = Constants.ANDROID_APP;

    @SerializedName("channel_version")
    private String channelVersion = BuildConfig.VERSION_NAME;

    @SerializedName("platform")
    private String platform = Constants.ANDROID;

    @SerializedName("platform_version")
    private String platformVersion = Build.VERSION.RELEASE;

    protected AmazonContentBodyRequest(Context context) {

        userUUID = context.getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getString(PASKOOCHEH_UUID, "");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        timestamp = simpleDateFormat.format(new Date());
    }
}
