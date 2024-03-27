package org.asl19.paskoocheh.pojo;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppDownloadInfoForVersionCodesTypeConverter {
    @TypeConverter
    public static List<AppDownloadInfoForVersionCode> toDevicesList(String value) {
        Type listType = new TypeToken<ArrayList<AppDownloadInfoForVersionCode>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromDevicesList(List<AppDownloadInfoForVersionCode> list) {
        return new Gson().toJson(list);
    }
}
