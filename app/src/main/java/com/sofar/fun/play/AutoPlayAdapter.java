package com.sofar.fun.play;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;

import io.reactivex.subjects.PublishSubject;

public class AutoPlayAdapter extends RecyclerAdapter<Feed> {

  PublishSubject<AutoPlaySignal> playSignal;

  public AutoPlayAdapter(@NonNull PublishSubject<AutoPlaySignal> playSignal) {
    this.playSignal = playSignal;
  }

  @Override
  protected int getItemLayoutId(int viewType) {
    return R.layout.fun_auto_play_item;
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    return new AutoPlayViewBinder(playSignal);
  }
}
