package com.appsbygreatness.ideasappformobiledevelopers.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.appsbygreatness.ideasappformobiledevelopers.dao.IdeaDao;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

@Database(entities = {Idea.class}, version = 1, exportSchema = false)
public abstract class IdeaDatabase extends RoomDatabase {

    public abstract IdeaDao ideaDao();
}
