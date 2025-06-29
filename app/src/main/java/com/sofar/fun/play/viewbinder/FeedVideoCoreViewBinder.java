//package com.sofar.fun.play.viewbinder;
//
//import android.util.Log;
//import android.view.TextureView;
//
//import com.sofar.R;
//import com.sofar.base.exception.SofarErrorConsumer;
//import com.sofar.base.viewbinder.ViewBinder;
//import com.sofar.fun.play.Feed;
//import com.sofar.fun.play.VideoContext;
//import com.sofar.fun.play.singal.VideoControlSignal;
//import com.sofar.fun.play.singal.VideoInfo;
//import com.sofar.fun.play.singal.VideoStateSignal;
//import com.sofar.player.core.OnPlayerListener;
//import com.sofar.player.core.VideoPlayer;
//import com.sofar.utility.ScheduleHandler;
//
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.subjects.PublishSubject;
//
//public class FeedVideoCoreViewBinder extends ViewBinder<Feed> {
//
//  private static final String TAG = "FeedVideoCoreViewBinder";
//
//  VideoPlayer player;
//  TextureView playerTexture;
//
//  Feed feed;
//
//  PublishSubject<VideoStateSignal> videoStatePublisher;
//  PublishSubject<VideoControlSignal> videoControlPublisher;
//
//  CompositeDisposable mAutoDisposables = new CompositeDisposable();
//
//  ScheduleHandler mProgressHandler = new ScheduleHandler(200, () -> {
//    if (player != null && videoControlPublisher != null) {
//      VideoInfo videoInfo = new VideoInfo();
//      videoInfo.current = player.getCurrent();
//      videoInfo.duration = player.getDuration();
//      videoControlPublisher.onNext(VideoControlSignal.UPDATE_PROGRESS.setTag(videoInfo));
//      VideoControlSignal.UPDATE_PROGRESS.reset();
//    }
//  });
//
//  Consumer<VideoControlSignal> mVideoControlSignalConsumer = videoControlSignal -> {
//    Log.d(TAG, "videoControlSignal=" + videoControlSignal);
//    switch (videoControlSignal) {
//      case START:
//        start();
//        break;
//      case STOP:
//        stop();
//        break;
//      case SEEK_TO_PERCENT:
//        if (player != null && videoControlSignal.getTag() instanceof Float) {
//          float percent = (float) videoControlSignal.getTag();
//          player.seekTo((long) (percent * player.getDuration()));
//        }
//        break;
//    }
//  };
//
//  @Override
//  protected void onCreate() {
//    super.onCreate();
//    playerTexture = bindView(R.id.player_texture);
//  }
//
//  @Override
//  protected void onBind(Feed data) {
//    super.onBind(data);
//    feed = data;
//  }
//
//  @Override
//  protected void onBindExtra(Object extra) {
//    super.onBindExtra(extra);
//    if (extra instanceof VideoContext) {
//      mAutoDisposables.clear();
//      videoControlPublisher = ((VideoContext) extra).mVideoControlPublisher;
//      mAutoDisposables.add(
//        videoControlPublisher.subscribe(mVideoControlSignalConsumer, new SofarErrorConsumer()));
//
//      videoStatePublisher = ((VideoContext) extra).mVideoStatePublisher;
//    }
//  }
//
//  private void start() {
//    stop();
//    player = buildPlayer();
//    player.start();
//  }
//
//  private void stop() {
//    if (player != null) {
//      player.stop();
//      player = null;
//    }
//  }
//
//  private VideoPlayer buildPlayer() {
//    VideoPlayer player = new VideoPlayer(playerTexture);
//    if (feed.videoUrl != null) {
//      player.setUri(feed.videoUrl);
//      player.setOnPlayerListener(new OnPlayerListener() {
//
//        @Override
//        public void onPlayerStart() {
//          changeVideoState(VideoStateSignal.PLAYING);
//          mProgressHandler.start();
//        }
//
//        @Override
//        public void onPlayerStop() {
//          changeVideoState(VideoStateSignal.INIT);
//          mProgressHandler.stop();
//        }
//
//        @Override
//        public void onPlayerResume() {
//          changeVideoState(VideoStateSignal.PLAYING);
//        }
//
//        @Override
//        public void onPlayerPause() {
//          changeVideoState(VideoStateSignal.PAUSE);
//        }
//
//        @Override
//        public void onPlayerCompleted() {
//          changeVideoState(VideoStateSignal.COMPLETE);
//        }
//      });
//    }
//    return player;
//  }
//
//  private void changeVideoState(VideoStateSignal state) {
//    if (videoStatePublisher != null) {
//      videoStatePublisher.onNext(state);
//    }
//  }
//
//  @Override
//  protected void onUnbind() {
//    super.onUnbind();
//    stop();
//  }
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    stop();
//    mAutoDisposables.clear();
//  }
//
//}
