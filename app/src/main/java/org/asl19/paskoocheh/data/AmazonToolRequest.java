package org.asl19.paskoocheh.data;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AmazonToolRequest extends AmazonContentBodyRequest {
    @SerializedName("tool")
    private String tool;

    @SerializedName("tool_version")
    private String toolVersion;

    @SerializedName("download_time")
    private String downloadTime;

    @SerializedName("ip_address")
    private String ipAddress;

    @SerializedName("downloaded_via")
    private String downloadedVia;

    @SerializedName("country")
    private String country;

    @SerializedName("network_country")
    private String networkCountry;

    @SerializedName("city")
    private String city;

    @SerializedName("network_type")
    private String networkType;

    @SerializedName("file_size")
    private String fileSize;

    @SerializedName("network_name")
    private String networkName;

    @SerializedName("user_id")
    private String userId;

    public AmazonToolRequest(Context context) {
        super(context);
    }
}
