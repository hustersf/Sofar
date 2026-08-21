package com.sofar.business.github.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.sofar.business.github.api.GithubService;
import com.sofar.business.github.db.GithubLocalCache;
import com.sofar.business.github.db.RepoDatabase;
import com.sofar.network.ApiProvider;
import com.sofar.utility.LogUtil;

import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.disposables.Disposable;

public class GithubRepository {

  private static final String TAG = "GithubRepository";

  @NonNull
  private GithubLocalCache cache;
  @NonNull
  private GithubService service;

  private Disposable disposable;

  private int lastRequestedPage = 1;
  private static final int NETWORK_PAGE_SIZE = 20;
  private static final String IN_QUALIFIER = "in:name,description";
  private boolean isRequestInProgress = false;
  MediatorLiveData<String> networkErrors = new MediatorLiveData<>();

  public GithubRepository(@NonNull Context context) {
    cache = new GithubLocalCache(RepoDatabase.getInstance(context).getRepoDao(), Executors.newSingleThreadExecutor());
    service = ApiProvider.getGithubService();
  }

  public RepoSearchResult search(String query) {
    LogUtil.d(TAG, "New query:" + query);
    lastRequestedPage = 1;
    requestAndSaveData(query);

    LiveData<List<Repo>> repos = cache.reposByName(query);

    return new RepoSearchResult(repos, networkErrors);
  }

  public void requestMore(String query) {
    requestAndSaveData(query);
  }

  private void requestAndSaveData(String query) {
    if (isRequestInProgress) {
      return;
    }

    isRequestInProgress = true;
    String apiQuery = query + IN_QUALIFIER;
    disposable = service.searchRepos(apiQuery, lastRequestedPage, NETWORK_PAGE_SIZE).subscribe(response -> {
      cache.insert(response.items, () -> {
        LogUtil.d(TAG, "fetch success page=" + lastRequestedPage + " query=" + query);
        lastRequestedPage++;
        isRequestInProgress = false;
      });
    }, throwable -> {
      networkErrors.postValue(throwable.toString());
      isRequestInProgress = false;
    });
  }

  public void clear() {
    if (disposable != null) {
      disposable.dispose();
    }
  }

}
