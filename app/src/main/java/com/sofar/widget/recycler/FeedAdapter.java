package com.sofar.widget.recycler;

import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.recycler.RecyclerViewHolder;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.widget.recycler.viewbinder.FeedViewBinder;

public class FeedAdapter extends RecyclerAdapter<Feed> {

  private static final String TAG = "FeedAdapter";

  @Override
  protected int getItemLayoutId(int viewType) {
    return R.layout.feed_item;
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    return new FeedViewBinder();
  }

  @Override
  public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Log.d(TAG, "onCreateViewHolder");
    return super.onCreateViewHolder(parent, viewType);
  }

  @Override
  public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    Log.d(TAG, "onBindViewHolder position=" + position);
    super.onBindViewHolder(holder, position);
  }

  @Override
  public void onViewRecycled(RecyclerViewHolder holder) {
    Log.d(TAG, "onViewRecycled position=" + holder.getLayoutPosition());
    super.onViewRecycled(holder);
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    Log.d(TAG, "onDetachedFromRecyclerView");
    super.onDetachedFromRecyclerView(recyclerView);
  }

}
