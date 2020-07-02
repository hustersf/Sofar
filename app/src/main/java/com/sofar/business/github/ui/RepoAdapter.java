package com.sofar.business.github.ui;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.business.github.model.Repo;
import com.sofar.business.github.viewbinder.RepoViewBinder;

public class RepoAdapter extends RecyclerAdapter<Repo> {
  @Override
  protected int getItemLayoutId(int viewType) {
    return R.layout.repo_item;
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    RecyclerViewBinder binder = new RecyclerViewBinder();
    binder.addViewBinder(new RepoViewBinder());
    return binder;
  }
}
