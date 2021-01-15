package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
public class Info {
    @SerializedName("fa")
    @Expose
    public LocalizedInfo fa;
    @SerializedName("en")
    @Expose
    public LocalizedInfo en;

    public Info() {}
}