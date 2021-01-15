package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ConfigGuideTutorial {
    @SerializedName("versions")
    @Expose
    public VersionsGuideTutorial versions;
}
