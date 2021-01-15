package org.asl19.paskoocheh.utils;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.asl19.paskoocheh.pojo.Image;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ImageArrayTypeConverter {
    @TypeConverter
    public static List<Image> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Image>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromList(List<Image> list) {
        return new Gson().toJson(list);
    }
}
