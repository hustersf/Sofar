package com.sofar.business.github.model;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RepoSearchResult {

  LiveData<List<Repo>> data;
  LiveData<String> networkErrors;

  public RepoSearchResult(LiveData<List<Repo>> data, LiveData<String> networkErrors) {
    this.data = data;
    this.networkErrors = networkErrors;
  }
}
