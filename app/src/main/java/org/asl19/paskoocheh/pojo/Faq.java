package org.asl19.paskoocheh.pojo;

import androidx.room.Entity;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Data;

@Parcel
@Data
@Entity(primaryKeys = {"toolId", "order", "language"})
public class Faq {

    public int toolId = 0;

    @SerializedName("answer")
    @Expose
    public String answer;

    @SerializedName("question")
    @Expose
    public String question;

    @SerializedName("order")
    @Expose
    @NonNull
    public Integer order = 0;

    @SerializedName("language")
    @Expose
    @NonNull
    public String language = "";

    public Faq() {}
}