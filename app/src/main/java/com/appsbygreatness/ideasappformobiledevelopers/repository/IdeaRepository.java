package com.appsbygreatness.ideasappformobiledevelopers.repository;

import android.content.Context;
import com.appsbygreatness.ideasappformobiledevelopers.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.dao.IdeaDao;
import com.appsbygreatness.ideasappformobiledevelopers.database.IdeaDatabase;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import java.util.List;

public class IdeaRepository {

    private IdeaDao ideaDao;

    public IdeaRepository(Context context){

        IdeaDatabase ideaDatabase = IdeaDatabase.getDatabase(context);
        ideaDao = ideaDatabase.ideaDao();
    }

    public void insertIdea(final Idea idea){

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                ideaDao.insertIdea(idea);
            }
        });


    }

    public void updateIdea(final Idea idea){

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                ideaDao.updateIdea(idea);
            }
        });


    }

    public void deleteIdea(final Idea idea){

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                ideaDao.deleteIdea(idea);
            }
        });
    }


    public Idea getIdea(final int id){

        return ideaDao.getIdea(id);
    }

    public List<Idea> getAllIdeas(){

        return ideaDao.getAllIdeas();
    }


}
