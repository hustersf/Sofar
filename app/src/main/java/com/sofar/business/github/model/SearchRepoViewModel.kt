package com.sofar.business.github.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SearchRepoViewModel extends ViewModel {

  private static final int VISIBLE_THRESHOLD = 5;

  @NonNull
  GithubRepository repository;

  MutableLiveData<String> queryLiveData = new MutableLiveData<>();

  LiveData<RepoSearchResult> repoResult = Transformations.map(queryLiveData, input -> repository.search(input));

  public LiveData<List<Repo>> repos = Transformations.switchMap(repoResult, input -> input.data);
  public LiveData<String> networkErrors = Transformations.switchMap(repoResult, input -> input.networkErrors);

  public SearchRepoViewModel(@NonNull GithubRepository repository) {
    this.repository = repository;
  }

  public void searchRepo(String query) {
    queryLiveData.postValue(query);
  }

  public void listScrolled(int visibleItemCount, int lastVisibleItemPosition, int totalItemCount) {
    if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
      repository.requestMore(queryLiveData.getValue());
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    repository.clear();
  }

}
