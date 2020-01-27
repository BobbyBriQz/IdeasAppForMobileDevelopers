package com.appsbygreatness.ideasappformobiledevelopers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

public class DetailedPageViewModel extends ViewModel {

    private LiveData<Idea> idea;

    public DetailedPageViewModel(IdeaRepository ideaRepository, int id){
        idea = ideaRepository.getIdea(id);
    }

    public LiveData<Idea> getIdea() {
        return idea;
    }
}
