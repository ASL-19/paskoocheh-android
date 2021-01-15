package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Entity
@Data
public class OsId {

    @SerializedName("display_name")
    @Expose
    public Name displayName;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;

}
