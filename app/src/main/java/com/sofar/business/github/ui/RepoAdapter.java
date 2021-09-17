package com.sofar.business.github.ui;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.business.github.model.Repo;
import com.sofar.business.github.viewbinder.RepoViewBinder;
import com.sofar.utility.ViewUtil;

public class RepoAdapter extends RecyclerAdapter<Repo> {

  @NonNull
  @Override
  protected View onCreateView(ViewGroup parent, int viewType) {
    return ViewUtil.inflate(parent, R.layout.repo_item);
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    RecyclerViewBinder binder = new RecyclerViewBinder();
    binder.addViewBinder(new RepoViewBinder());
    return binder;
  }
}
