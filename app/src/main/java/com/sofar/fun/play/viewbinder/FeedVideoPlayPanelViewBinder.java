package com.sofar.fun.play.viewbinder;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sofar.R;
import com.sofar.base.exception.SofarErrorConsumer;
import com.sofar.base.viewbinder.ViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.fun.play.VideoContext;
import com.sofar.fun.play.singal.VideoControlSignal;
import com.sofar.fun.play.singal.VideoInfo;
import com.sofar.fun.play.singal.VideoStateSignal;
import com.sofar.utility.TimeUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class FeedVideoPlayPanelViewBinder extends ViewBinder<Feed> {

  private static final String TAG = "FeedVideoPlayPanel";

  ViewGroup playRootView;

  TextView videoCurrent;
  TextView videoDuration;
  SeekBar seekBar;

  long duration;

  CompositeDisposable mAutoDisposables = new CompositeDisposable();
  PublishSubject<VideoStateSignal> videoStatePublisher;
  PublishSubject<VideoControlSignal> videoControlPublisher;

  Consumer<VideoStateSignal> mVideoStateSignalConsumer = videoStateSignal -> {
    Log.d(TAG, "videoStateSignal=" + videoStateSignal);
  };

  Consumer<VideoControlSignal> mVideoControlSignalConsumer = videoControlSignal -> {
    if (videoControlSignal != VideoControlSignal.UPDATE_PROGRESS) {
      Log.d(TAG, "videoControlSignal=" + videoControlSignal);
    }
    switch (videoControlSignal) {
      case UPDATE_PROGRESS:
        if (videoControlSignal.getTag() instanceof VideoInfo) {
          VideoInfo info = (VideoInfo) videoControlSignal.getTag();
          videoCurrent.setText(TimeUtil.timeSpreadFormat(info.current));
          videoDuration.setText(TimeUtil.timeSpreadFormat(info.duration));
          if (info.duration > 0) {
            duration = info.duration;
            int progress = (int) (1.0f * info.current / info.duration * seekBar.getMax());
            seekBar.setProgress(progress);
          }
        }
        break;
    }
  };

  @Override
  protected void onCreate() {
    super.onCreate();
    playRootView = bindView(R.id.play_panel_root);
    videoCurrent = bindView(R.id.video_current);
    videoDuration = bindView(R.id.video_duration);
    seekBar = bindView(R.id.video_seekbar);

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          long current = (long) (1.0f * progress / seekBar.getMax() * duration);
          videoCurrent.setText(TimeUtil.timeSpreadFormat(current));
          Log.d(TAG,"seek="+progress+" dur="+duration);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        float percent = 1.0f * seekBar.getProgress() / seekBar.getMax();
        if (videoControlPublisher != null) {
          videoControlPublisher.onNext(VideoControlSignal.SEEK_TO_PERCENT.setTag(percent));
          VideoControlSignal.SEEK_TO_PERCENT.reset();
        }
      }
    });
  }

  @Override
  protected void onBind(Feed data) {
    super.onBind(data);
  }

  @Override
  protected void onBindExtra(Object extra) {
    super.onBindExtra(extra);
    if (extra instanceof VideoContext) {
      mAutoDisposables.clear();
      videoStatePublisher = ((VideoContext) extra).mVideoStatePublisher;
      videoControlPublisher = ((VideoContext) extra).mVideoControlPublisher;
      mAutoDisposables.add(
        videoStatePublisher.subscribe(mVideoStateSignalConsumer, new SofarErrorConsumer()));
      mAutoDisposables.add(
        videoControlPublisher.subscribe(mVideoControlSignalConsumer, new SofarErrorConsumer()));
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mAutoDisposables.clear();
  }
}
