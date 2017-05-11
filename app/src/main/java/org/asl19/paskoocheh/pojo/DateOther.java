package org.asl19.paskoocheh.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

/**
 * DateOther Gson Pojo.
 */
@Parcel
@Data
public class DateOther {

    @SerializedName("gdate")
    @Expose
    String gdate;
    @SerializedName("gshortdate")
    @Expose
    String gshortdate;
    @SerializedName("jdate")
    @Expose
    String jdate;
    @SerializedName("jshortdate")
    @Expose
    String jshortdate;
    @SerializedName("djdate")
    @Expose
    String djdate;
    @SerializedName("dgdate")
    @Expose
    String dgdate;

    public DateOther() {}
}