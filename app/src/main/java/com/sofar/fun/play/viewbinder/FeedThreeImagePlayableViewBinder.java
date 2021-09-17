package com.sofar.fun.play.viewbinder;

import java.util.List;

import android.util.Log;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.fun.play.Feed;
import com.sofar.fun.play.core.FeedPlayer;
import com.sofar.fun.play.core.ImageArrayPlayer;
import com.sofar.fun.play.core.PlayCallback;
import com.sofar.fun.play.core.PlayableViewBinder;
import com.sofar.image.widget.SofarImageView;
import com.sofar.utility.CollectionUtil;
import com.sofar.utility.ViewUtil;

public class FeedThreeImagePlayableViewBinder extends PlayableViewBinder<Feed> {

  private static final String TAG = "FeedImagePlayable";

  ViewGroup imageContainer;

  SofarImageView cover1;
  SofarImageView cover2;
  SofarImageView cover3;
  Feed feed;

  ImageArrayPlayer mImageArrayPlayer;
  FeedPlayer innerFeedPlayer;
  FeedPlayer outerFeedPlayer;

  PlayCallback innerPlayCallback = new PlayCallback() {
    @Override
    public void onAllFinished() {
      Log.d(TAG, "onAllFinished");
      if (outerFeedPlayer != null) {
        outerFeedPlayer.playFinished(FeedThreeImagePlayableViewBinder.this);
      }
    }
  };

  public FeedThreeImagePlayableViewBinder(FeedPlayer player) {
    outerFeedPlayer = player;
  }

  @Override
  protected void onCreate() {
    super.onCreate();
    imageContainer = bindView(R.id.image_container);
    cover1 = view.findViewById(R.id.cover1);
    cover2 = view.findViewById(R.id.cover2);
    cover3 = view.findViewById(R.id.cover3);
    innerFeedPlayer = FeedPlayer.create();
    innerFeedPlayer.callback(innerPlayCallback);
    mImageArrayPlayer = new ImageArrayPlayer(innerFeedPlayer);
  }

  @Override
  protected void onBind(Feed data) {
    super.onBind(data);
    feed = data;
    List<String> list = feed.imgUrls;
    mImageArrayPlayer.clear();
    if (!CollectionUtil.isEmpty(list)) {
      for (int i = 0; i < list.size(); i++) {
        String url = list.get(i);
        if (i == 0) {
          mImageArrayPlayer.addImage(cover1, url);
        } else if (i == 1) {
          mImageArrayPlayer.addImage(cover2, url);
        } else if (i == 2) {
          mImageArrayPlayer.addImage(cover3, url);
        }
      }
    }
  }

  @Override
  public boolean canPlay(RecyclerView.ViewHolder holder, int adapterPosition) {
    return true;
  }

  @Override
  public float getViewShowRatio() {
    return ViewUtil.getViewShowRatio(imageContainer);
  }

  @Override
  public void start() {
    mImageArrayPlayer.start();
  }

  @Override
  public void stop() {
    mImageArrayPlayer.stop();
  }

  @Override
  public boolean canPlay() {
    return true;
  }
}
