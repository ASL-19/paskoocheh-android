package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Entity
@Data
@Parcel
public class Tool {
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("twitter")
    @Expose
    public String twitter;
    @SerializedName("featured")
    @Expose
    public Boolean featured = false;
    @SerializedName("facebook")
    @Expose
    public String facebook;
    @Ignore
    @SerializedName("images")
    @Expose
    public Images images;
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("trusted")
    @Expose
    public Boolean trusted;
    @SerializedName("rss")
    @Expose
    public String rss;
    @Ignore
    @SerializedName("info")
    @Expose
    public Info info;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("blog")
    @Expose
    public String blog;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("opensource")
    @Expose
    public Boolean opensource;
    @SerializedName("contact_email")
    @Expose
    public String contactEmail;
    @SerializedName("contact_url")
    @Expose
    public String contactUrl;

    public Tool() {}
}