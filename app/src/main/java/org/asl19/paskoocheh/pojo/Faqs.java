package org.asl19.paskoocheh.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import lombok.Data;

@Parcel
@Data
public class Faqs {
    @SerializedName("tool_id")
    @Expose
    public Integer toolId;
    @SerializedName("app_name")
    @Expose
    public String appName;
    @SerializedName("faq")
    @Expose
    public List<Faq> faq = null;
    @SerializedName("os_id")
    @Expose
    public String osId;
    @SerializedName("faq_urls")
    @Expose
    public String faqUrls;
    @SerializedName("last_modified")
    @Expose
    public String lastModified;
    @SerializedName("id")
    @Expose
    public Integer id;

    public Faqs() {}
}
