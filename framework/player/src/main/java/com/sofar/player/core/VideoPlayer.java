package com.sofar.player.core;

import android.content.Context;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.video.VideoListener;

/**
 * 封装视频播放器的使用
 * 基于 ExoPlayer
 */
public class VideoPlayer {

  private static final String TAG = "VideoPlayer";

  @NonNull
  SimpleExoPlayer player;
  @NonNull
  final Context context;

  boolean prepared;

  OnVideoListener outListener;

  VideoListener videoListener = new VideoListener() {
    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
      Log.d(TAG, "onVideoSizeChanged width=" + width + " height" + height);
    }

    @Override
    public void onSurfaceSizeChanged(int width, int height) {
      Log.d(TAG, "onSurfaceSizeChanged width=" + width + " height" + height);
    }

    @Override
    public void onRenderedFirstFrame() {
      Log.d(TAG, "onRenderedFirstFrame");
      if (outListener != null) {
        outListener.onVideoStart();
      }
    }
  };

  Player.EventListener eventListener = new Player.EventListener() {
    @Override
    public void onPlaybackStateChanged(int state) {
      Log.d(TAG, "onPlaybackStateChanged:state=" + state);
      if (state == 4) {
        Log.d(TAG, "completed");
        if (outListener != null) {
          outListener.onVideoCompleted();
        }
      }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
      Log.d(TAG, "onPlayerError=" + error.getMessage());
      if (outListener != null) {
        outListener.onVideoError();
      }
    }
  };

  public VideoPlayer(@NonNull String uri, @NonNull TextureView textureView) {
    context = textureView.getContext();
    player = new SimpleExoPlayer.Builder(context).build();
    player.setVideoTextureView(textureView);
    MediaItem mediaItem = MediaItem.fromUri(uri);
    player.addMediaItem(mediaItem);
  }

  public void prepare() {
    player.addVideoListener(videoListener);
    player.addListener(eventListener);
    player.prepare();
    prepared = true;
  }

  /**
   * 是否准备好
   */
  public boolean isPrepared() {
    return prepared;
  }

  public void start() {
    Log.d(TAG, "start");
    if (isPrepared()) {
      resume();
      return;
    }

    prepare();
    player.play();
  }

  public void resume() {
    Log.d(TAG, "resume");
    player.play();
    if (outListener != null) {
      outListener.onVideoResume();
    }
  }

  public void pause() {
    Log.d(TAG, "pause");
    player.pause();
    if (outListener != null) {
      outListener.onVideoPause();
    }
  }

  public void stop() {
    Log.d(TAG, "stop");
    player.stop();
    player.release();
    player.removeVideoListener(videoListener);
    player.removeListener(eventListener);
    if (outListener != null) {
      outListener.onVideoStop();
    }
  }

  public void setOnVideoListener(OnVideoListener listener) {
    this.outListener = listener;
  }

  public interface OnVideoListener {

    void onVideoStart();

    void onVideoResume();

    void onVideoPause();

    void onVideoStop();

    void onVideoCompleted();

    void onVideoError();
  }
}
