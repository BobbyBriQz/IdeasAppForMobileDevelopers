package com.appsbygreatness.ideasappformobiledevelopers;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

public class DetailedPageViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final IdeaRepository ideaRepository;
    private final int ideaId;


    public DetailedPageViewModelFactory(IdeaRepository ideaRepository, int ideaId) {
        this.ideaRepository = ideaRepository;
        this.ideaId = ideaId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailedPageViewModel(ideaRepository, ideaId);
    }
}
