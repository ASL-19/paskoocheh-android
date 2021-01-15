package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
public class GuideTutorial {

    @SerializedName("tool_id")
    @Expose
    public Integer toolId;
    @SerializedName("app_name")
    @Expose
    public String appName;
    @SerializedName("guide")
    @Expose
    public Guide[] guide;
    @SerializedName("os_id")
    @Expose
    public String osId;
    @SerializedName("last_modified")
    @Expose
    public String lastModified;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("tutorial")
    @Expose
    public Tutorial[] tutorial;

    public GuideTutorial() {}
}
