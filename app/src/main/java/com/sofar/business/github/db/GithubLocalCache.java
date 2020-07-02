package com.sofar.business.github.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.sofar.business.github.model.Repo;

import java.util.List;
import java.util.concurrent.Executor;

public class GithubLocalCache {

  @NonNull
  private RepoDao repoDao;
  @NonNull
  private Executor ioExecutor;

  public GithubLocalCache(@NonNull RepoDao repoDao, @NonNull Executor ioExecutor) {
    this.repoDao = repoDao;
    this.ioExecutor = ioExecutor;
  }

  public void insert(List<Repo> repos, Runnable runnable) {
    ioExecutor.execute(() -> {
      repoDao.insert(repos);
      if (runnable != null) {
        runnable.run();
      }
    });
  }

  public LiveData<List<Repo>> reposByName(String name) {
    String query = "%" + name.replace(' ', '%') + "%";
    return repoDao.reposByName(query);
  }
}
