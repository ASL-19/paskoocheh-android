package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Config {

    @SerializedName("versions")
    @Expose
    public Versions versions;
    @SerializedName("tools")
    @Expose
    public Tool[] tools = null;
    @SerializedName("categories")
    @Expose
    public Category[] categories = null;
    @SerializedName("os_ids")
    @Expose
    public OsId[] osIds = null;
}
