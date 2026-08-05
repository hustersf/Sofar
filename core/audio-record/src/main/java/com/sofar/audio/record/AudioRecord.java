package com.sofar.audio.record;

import android.Manifest;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * 音频录制
 * 使用之前，先申请权限 {@link Manifest.permission#RECORD_AUDIO}
 * 并且保证传递进来的文件路径的父目录已存在
 */
public class AudioRecord {

  private static final String TAG = "AudioRecord";

  /**
   * 音频文件保存地址
   */
  @NonNull
  private String filePath;

  @NonNull
  private MediaRecorder mediaRecorder;
  boolean start = false;

  MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
      Log.d(TAG, "error=" + what + ":" + extra);
    }
  };

  public AudioRecord(@NonNull String filePath) {
    this.filePath = filePath;
    mediaRecorder = new MediaRecorder();
    mediaRecorder.setOnErrorListener(errorListener);
  }


  private void init() {
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setAudioSamplingRate(44100);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    mediaRecorder.setOutputFile(filePath);
  }

  /**
   * 开始录制
   */
  public void start() {
    try {
      if (start) {
        Log.d(TAG, "audio has start");
        return;
      }

      init();
      mediaRecorder.prepare();
      mediaRecorder.start();
      start = true;
    } catch (Exception e) {
      e.printStackTrace();
      Log.d(TAG, "start error=" + e.toString());
    }
  }

  /**
   * 结束录制
   */
  public boolean stop() {
    try {
      if (start) {
        mediaRecorder.stop();
        mediaRecorder.reset();
        start = false;
        return true;
      } else {
        Log.d(TAG, "audio has not start");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.d(TAG, "error=" + e.toString());
    }
    return false;
  }

}
