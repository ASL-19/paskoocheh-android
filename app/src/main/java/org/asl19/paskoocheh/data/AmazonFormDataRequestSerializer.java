package org.asl19.paskoocheh.data;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AmazonFormDataRequestSerializer implements JsonSerializer<AmazonFormDataRequest>{
    @Override
    public JsonElement serialize(AmazonFormDataRequest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        //object.add("acl", context.serialize(src.getAcl()));
        object.add("key", context.serialize(src.getKey()));
        //object.add("policy", context.serialize(src.getPolicy()));
        //object.add("x-amz-algorithm", context.serialize(src.getXAmzAlgorithm()));
        //object.add("x-amz-credential", context.serialize(src.getXAmzCredential()));
        //object.add("x-amz-date", context.serialize(src.getXAmzDate()));
        //object.add("x-amz-signature", context.serialize(src.getXAmzSignature()));
        //object.add("file", context.serialize(src.getFile()));
        return object;
    }
}
