package org.asl19.paskoocheh.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
@Entity
public class Guide {
    public int toolId;

    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("headline")
    @Expose
    public String headline;
    @SerializedName("order")
    @Expose
    public Integer order;
    @SerializedName("last_modified")
    @Expose
    public String lastModified;
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;

    public Guide() {}
}