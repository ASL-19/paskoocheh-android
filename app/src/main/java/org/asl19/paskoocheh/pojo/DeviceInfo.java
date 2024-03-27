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
public class DeviceInfo {
    @SerializedName("device")
    @Expose
    public String deviceName;

    @SerializedName("version_code")
    @Expose
    public Integer versionCode;

    public DeviceInfo() {}
}
