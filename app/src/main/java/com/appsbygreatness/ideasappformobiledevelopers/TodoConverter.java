package com.appsbygreatness.ideasappformobiledevelopers;

import androidx.room.TypeConverter;

import com.appsbygreatness.ideasappformobiledevelopers.model.Todo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TodoConverter {

    @TypeConverter
    public static List<Todo> fromString(String string){

        Gson gson = new Gson();
        Type type = new TypeToken<List<Todo>>(){}.getType();
        List<Todo> list;

        if(string  != null){

            list = gson.fromJson(string, type );
        }else {
            list = new ArrayList<>();
        }

        return list;
    }

    @TypeConverter
    public static String listToString(List<Todo> list){

        Gson gson = new Gson();
        Type type = new TypeToken<List<Todo>>(){}.getType();
        String string;

        if(list != null){
            string = gson.toJson(list, type);

        }else {
            List<Todo> emptyArray = new ArrayList<>();
            string = gson.toJson(emptyArray);
        }

        return string;
    }
}
