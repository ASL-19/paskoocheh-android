package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Data
@Parcel
@Entity
public class Name {

    @PrimaryKey
    public Integer categoryId;

    public String icon;

    @SerializedName("fa")
    @Expose
    public String fa;
    @SerializedName("en")
    @Expose
    public String en;

    public Name() {}
}
