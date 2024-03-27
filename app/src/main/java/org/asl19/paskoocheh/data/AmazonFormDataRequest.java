package org.asl19.paskoocheh.data;


import com.google.gson.annotations.SerializedName;

import org.asl19.paskoocheh.Constants;

import lombok.Data;

@Data
public class AmazonFormDataRequest {

    //@SerializedName("acl")
    //private String acl = Constants.ACL;

    @SerializedName("key")
    private String key = "";

    //@SerializedName("policy")
    //private String policy = Constants.POLICY;

    //@SerializedName("x-amz-algorithm")
    //private String xAmzAlgorithm = Constants.X_AMZ_ALGORITHM;

    //@SerializedName("x-amz-credential")
    //private String xAmzCredential = Constants.X_AMZ_CREDENTIAL;

    //@SerializedName("x-amz-date")
    //private String xAmzDate = Constants.X_AMZ_DATE;

    //@SerializedName("x-amz-signature")
    //private String xAmzSignature = Constants.X_AMZ_SIGNATURE;

    @SerializedName("file")
    private byte[] file;

    public AmazonFormDataRequest() {
    }
}

