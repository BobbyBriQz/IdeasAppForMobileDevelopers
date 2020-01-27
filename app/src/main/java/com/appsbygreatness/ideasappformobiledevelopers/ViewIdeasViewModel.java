package com.appsbygreatness.ideasappformobiledevelopers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

import java.util.List;

public class ViewIdeasViewModel extends AndroidViewModel {

    LiveData<List<Idea>> ideas;
    public ViewIdeasViewModel(@NonNull Application application) {
        super(application);
        IdeaRepository ideaRepository = new IdeaRepository(application);
        ideas = ideaRepository.getAllIdeas();
    }

    public LiveData<List<Idea>> getIdeas() {
        return ideas;
    }
}
