package com.sofar.fun.play.core;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Animatable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.fresco.animation.drawable.BaseAnimationListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.sofar.image.widget.SofarImageView;

/**
 * 按照添加的顺序依次播放
 */
public class ImageArrayPlayer {

  List<Playable> mPlayableList = new ArrayList<>();
  FeedPlayer mFeedPlayer;

  public ImageArrayPlayer(@NonNull FeedPlayer player) {
    this.mFeedPlayer = player;
  }

  public void addImage(SofarImageView cover, String url) {
    mPlayableList.add(new AnimPlayable(mFeedPlayer, cover, url));
  }

  public void start() {
    mFeedPlayer.feed(mPlayableList);
    mFeedPlayer.start();
  }

  public void stop() {
    mFeedPlayer.stop();
  }

  public void clear() {
    mPlayableList.clear();
  }

  private static class AnimPlayable implements Playable {

    private static final String TAG = "AnimPlayable";

    @NonNull final FeedPlayer player;
    SofarImageView cover;
    String url;

    Animatable animatable;

    boolean canStart = false;

    BaseControllerListener<ImageInfo> listener = new BaseControllerListener<ImageInfo>() {
      @Override
      public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo,
        @Nullable Animatable animatable) {
        super.onFinalImageSet(id, imageInfo, animatable);
        AnimPlayable.this.animatable = animatable;
        Log.d(TAG, "onFinalImageSet url=" + url);
        startInternal();
      }

      @Override
      public void onFailure(String id, Throwable throwable) {
        super.onFailure(id, throwable);
        Log.d(TAG, "onFailure url=" + url);
        player.playFinished(AnimPlayable.this);
      }
    };

    BaseAnimationListener animListener = new BaseAnimationListener() {
      int repeatCount = 0;

      @Override
      public void onAnimationStart(AnimatedDrawable2 drawable) {
        Log.d(TAG, "onAnimationStart url=" + url);
        super.onAnimationStart(drawable);
        repeatCount = 0;
      }

      @Override
      public void onAnimationStop(AnimatedDrawable2 drawable) {
        Log.d(TAG, "onAnimationStop url=" + url);
        super.onAnimationStop(drawable);
      }

      @Override
      public void onAnimationRepeat(AnimatedDrawable2 drawable) {
        super.onAnimationRepeat(drawable);
        Log.d(TAG, "onAnimationRepeat url=" + url);
        if (repeatCount >= 1) {
          player.playFinished(AnimPlayable.this);
        }
        repeatCount++;
      }
    };

    public AnimPlayable(@NonNull FeedPlayer player, SofarImageView cover, String url) {
      this.player = player;
      this.cover = cover;
      this.url = url;
      cover.bindUrl(url, listener);
    }

    @Override
    public void start() {
      canStart = true;
      startInternal();
    }

    @Override
    public void stop() {
      canStart = false;
      if (animatable != null) {
        animatable.stop();
      }
    }

    @Override
    public boolean canPlay() {
      return false;
    }

    private void startInternal() {
      if (!canStart) {
        return;
      }

      Log.d(TAG, "startInternal url=" + url);
      if (animatable instanceof AnimatedDrawable2) {
        AnimatedDrawable2 animatedDrawable2 = (AnimatedDrawable2) animatable;
        animatedDrawable2.setAnimationListener(animListener);
        animatable.start();
      } else {
        player.playFinished(AnimPlayable.this);
      }
    }
  }

}
