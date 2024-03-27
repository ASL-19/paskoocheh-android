package org.asl19.paskoocheh.data;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AmazonReportDeviceRequest extends AmazonContentBodyRequest {

    @SerializedName("id")
    private String id;

    @SerializedName("device")
    private String device;

    @SerializedName("hardware")
    private String hardware;

    @SerializedName("product")
    private String product;

    @SerializedName("model")
    private String model;

    @SerializedName("radio")
    private String radio;

    @SerializedName("bootloader")
    private String bootloader;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("brand")
    private String brand;

    @SerializedName("version_sdk")
    private Integer versionSdk;

    @SerializedName("manufacturer")
    private String manufacturer;

    @SerializedName("version_release")
    private String versionRelease;

    public AmazonReportDeviceRequest(Context context) {
        super(context);
    }
}
