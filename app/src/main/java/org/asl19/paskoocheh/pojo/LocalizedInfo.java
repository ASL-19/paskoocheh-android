package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Entity(primaryKeys = {"toolId", "locale"})
@Parcel
@Data
public class LocalizedInfo {

    @NonNull
    public String locale = "";

    public int toolId;
    @SerializedName("company")
    @Expose
    public String company = "";
    @SerializedName("last_modified")
    @Expose
    public String lastModified = "";
    @SerializedName("name")
    @Expose
    public String name = "";
    @SerializedName("description")
    @Expose
    public String description = "";

    public LocalizedInfo() {}
}
