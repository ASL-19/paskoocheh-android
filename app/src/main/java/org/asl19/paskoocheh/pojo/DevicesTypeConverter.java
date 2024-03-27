package org.asl19.paskoocheh.pojo;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DevicesTypeConverter {
    @TypeConverter
    public static List<DeviceInfo> toDevicesList(String value) {
        Type listType = new TypeToken<ArrayList<DeviceInfo>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromDevicesList(List<DeviceInfo> list) {
        return new Gson().toJson(list);
    }
}
