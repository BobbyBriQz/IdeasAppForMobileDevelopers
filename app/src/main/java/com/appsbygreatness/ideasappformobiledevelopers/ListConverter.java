package com.appsbygreatness.ideasappformobiledevelopers;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListConverter {

    @TypeConverter
    public static List<String> fromString(String string){

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> list;

        if(string  != null){

            list = gson.fromJson(string, type );
        }else {
            list = new ArrayList<>();
        }

        return list;
    }

    @TypeConverter
    public static String listToString(List<String> list){

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        String string;

        if(list != null){
            string = gson.toJson(list, type);

        }else {
            List<String> emptyArray = new ArrayList<>();
            string = gson.toJson(emptyArray);
        }

        return string;
    }
}
