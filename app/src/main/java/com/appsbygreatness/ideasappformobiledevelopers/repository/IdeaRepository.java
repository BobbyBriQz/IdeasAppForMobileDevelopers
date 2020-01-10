package com.appsbygreatness.ideasappformobiledevelopers.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.appsbygreatness.ideasappformobiledevelopers.database.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.database.IdeaDatabase;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

import java.util.ArrayList;
import java.util.List;

public class IdeaRepository {

    private String DB_NAME = "idea_database.db";

    private IdeaDatabase ideaDatabase;

    public IdeaRepository(Context context){

        ideaDatabase = Room.databaseBuilder(context, IdeaDatabase.class, DB_NAME).build();
    }

    public void insertIdea(final Idea idea){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                ideaDatabase.ideaDao().insertIdea(idea);
                return null;
            }
        }.execute();
    }

    public void updateIdea(final Idea idea){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                ideaDatabase.ideaDao().updateIdea(idea);
                return null;
            }
        }.execute();
    }

    public void deleteIdea(final Idea idea){

        ideaDatabase.ideaDao().deleteIdea(idea);
    }

    public void deleteIdea(final int id){

        ideaDatabase.ideaDao().deleteIdea(id);
    }



    public Idea getIdea(final int id){

        return ideaDatabase.ideaDao().getIdea(id);
    }

    public List<Idea> getAllIdeas(){

        return ideaDatabase.ideaDao().getAllIdeas();
    }


}
