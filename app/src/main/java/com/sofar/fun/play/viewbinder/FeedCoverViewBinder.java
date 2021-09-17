package com.sofar.fun.play.viewbinder;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.image.widget.SofarImageView;

public class FeedCoverViewBinder extends RecyclerViewBinder<Feed> {

  SofarImageView cover;
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
    cover.bindUrl(feed.imgUrl);
  }

  @Override
  protected void onUnbind() {
    super.onUnbind();
    if (feed != null) {
      Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(feed.imgUrl));
    }
  }
}
