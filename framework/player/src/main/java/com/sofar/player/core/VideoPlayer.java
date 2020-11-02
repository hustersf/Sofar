package com.sofar.player.core;

import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.video.VideoListener;

/**
 * 视频播放器
 */
public class VideoPlayer extends BasePlayer {

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
      if (playerListener != null) {
        playerListener.onPlayerStart();
      }
    }
  };

  public VideoPlayer(@NonNull TextureView textureView) {
    super(textureView.getContext());
    player.setVideoTextureView(textureView);
    TAG = "VideoPlayer";
  }

  public VideoPlayer(@NonNull String uri, @NonNull TextureView textureView) {
    super(textureView.getContext(), uri);
    player.setVideoTextureView(textureView);
    TAG = "VideoPlayer";
  }

  @Override
  public void prepare() {
    super.prepare();
    player.addVideoListener(videoListener);
  }

  @Override
  public void stop() {
    super.stop();
    player.removeVideoListener(videoListener);
  }
}
