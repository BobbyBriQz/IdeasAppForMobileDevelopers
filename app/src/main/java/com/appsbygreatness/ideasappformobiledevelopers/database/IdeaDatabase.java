package com.appsbygreatness.ideasappformobiledevelopers.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.appsbygreatness.ideasappformobiledevelopers.dao.IdeaDao;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

@Database(entities = {Idea.class}, version = 1, exportSchema = false)
public abstract class IdeaDatabase extends RoomDatabase {

    public abstract IdeaDao ideaDao();

    private static volatile IdeaDatabase INSTANCE;
    private static String DB_NAME = "idea_database.db";

    public static IdeaDatabase getDatabase(Context context){

        if(INSTANCE == null){
            synchronized (IdeaDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            IdeaDatabase.class, DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
