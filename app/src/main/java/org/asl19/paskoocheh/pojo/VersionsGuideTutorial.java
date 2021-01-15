package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import lombok.Data;

@Data
@Parcel
public class VersionsGuideTutorial {

    @SerializedName("android")
    @Expose
    public List<GuideTutorial> android = null;

    public VersionsGuideTutorial() {}
}