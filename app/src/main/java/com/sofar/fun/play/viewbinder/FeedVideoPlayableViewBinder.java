package com.sofar.fun.play.viewbinder;

import android.util.Log;
import android.view.ViewGroup;

import com.sofar.R;
import com.sofar.base.exception.SofarErrorConsumer;
import com.sofar.fun.play.Feed;
import com.sofar.fun.play.VideoContext;
import com.sofar.fun.play.core.FeedPlayer;
import com.sofar.fun.play.core.PlayableRecyclerViewBinder;
import com.sofar.fun.play.core.PlayableViewBinder;
import com.sofar.fun.play.singal.VideoControlSignal;
import com.sofar.fun.play.singal.VideoStateSignal;
import com.sofar.utility.ViewUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class FeedVideoPlayableViewBinder extends PlayableViewBinder<Feed> {

  private static final String TAG = "FeedVideoPlayable";

  ViewGroup videoRootView;

  FeedPlayer mFeedPlayer;
  PlayableRecyclerViewBinder parent;

  PublishSubject<VideoControlSignal> videoControlPublisher;
  PublishSubject<VideoStateSignal> videoStatePublisher;

  CompositeDisposable mAutoDisposables = new CompositeDisposable();

  Consumer<VideoStateSignal> mVideoStateSignalConsumer = videoStateSignal -> {
    Log.d(TAG, "videoStateSignal=" + videoStateSignal);
    switch (videoStateSignal) {
      case COMPLETE:
        if (mFeedPlayer != null) {
          mFeedPlayer.playFinished(parent);
        }
        break;
    }
  };


  public FeedVideoPlayableViewBinder(FeedPlayer feedPlayer, PlayableRecyclerViewBinder parent) {
    this.mFeedPlayer = feedPlayer;
    this.parent = parent;
  }

  @Override
  protected void onCreate() {
    super.onCreate();
    videoRootView = bindView(R.id.video_root);
  }

  @Override
  protected void onBindExtra(Object extra) {
    super.onBindExtra(extra);
    if (extra instanceof VideoContext) {
      mAutoDisposables.clear();
      videoControlPublisher = ((VideoContext) extra).mVideoControlPublisher;
      videoStatePublisher = ((VideoContext) extra).mVideoStatePublisher;
      mAutoDisposables.add(
        videoStatePublisher.subscribe(mVideoStateSignalConsumer, new SofarErrorConsumer()));
    }
  }

  @Override
  public float getViewShowRatio() {
    return ViewUtil.getViewShowRatio(videoRootView);
  }

  @Override
  public void start() {
    if (videoControlPublisher != null) {
      videoControlPublisher.onNext(VideoControlSignal.START);
    }
  }

  @Override
  public void stop() {
    if (videoControlPublisher != null) {
      videoControlPublisher.onNext(VideoControlSignal.STOP);
    }
  }

  @Override
  public boolean canPlay() {
    return true;
  }
}
