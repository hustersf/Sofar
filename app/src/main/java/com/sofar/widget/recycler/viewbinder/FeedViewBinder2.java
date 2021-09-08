package com.sofar.widget.recycler.viewbinder;

import android.widget.ImageView;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.Feed;

public class FeedViewBinder2 extends RecyclerViewBinder<Feed> {

  ImageView cover;
  Feed feed;

  @Override
  protected void onCreate() {
    super.onCreate();
    cover = view.findViewById(R.id.cover);
  }

  @Override
  protected void onBind(Feed data) {
    super.onBind(data);
    feed = data;
    cover.setImageResource(feed.drawableId);
  }

  @Override
  protected void onUnbind() {
    super.onUnbind();
  }
}
