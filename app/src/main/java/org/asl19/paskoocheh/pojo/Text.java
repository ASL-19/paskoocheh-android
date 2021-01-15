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
public class Text {

    @SerializedName("about")
    @Expose
    public String about;
    @SerializedName("last_modified")
    @Expose
    public String lastModified;
    @SerializedName("contact_email")
    @Expose
    public String contactEmail;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("privacy_policy")
    @Expose
    public String privacyPolicy;
    @SerializedName("terms_of_service")
    @Expose
    public String termsOfService;
    @SerializedName("terms_and_privacy")
    @Expose
    public String termsAndPrivacy;
    @SerializedName("id")
    @Expose
    @PrimaryKey
    public Integer id;

    public Text() {}
}