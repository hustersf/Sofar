package com.sofar.business.github.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SearchRepoViewModelFactory implements ViewModelProvider.Factory {

  @NonNull
  private GithubRepository repository;

  public SearchRepoViewModelFactory(@NonNull GithubRepository repository) {
    this.repository = repository;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new SearchRepoViewModel(repository);
  }
}
