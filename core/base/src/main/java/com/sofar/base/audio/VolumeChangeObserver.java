package com.sofar.base.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;
import androidx.annotation.NonNull;

public class VolumeChangeObserver {

  private static final String TAG = "VolumeChangeObserver";

  private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
  private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

  @NonNull
  private Context mContext;
  @NonNull
  private VolumeBroadcastReceiver mReceiver;
  private AudioManager mAudioManager;
  private Callback mCallback;

  public VolumeChangeObserver(@NonNull Context context) {
    mContext = context;
    mReceiver = new VolumeBroadcastReceiver();
    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
  }


  public void register() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(VOLUME_CHANGED_ACTION);
    mContext.registerReceiver(mReceiver, filter);
  }


  public void unregister() {
    mContext.unregisterReceiver(mReceiver);
  }


  public int getMaxVolume() {
    return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
  }

  public int getCurrentVolume() {
    return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
  }

  public void setVolume(int volume) {
    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
  }

  public void volumeChanged() {
    if (mCallback != null) {
      mCallback.onVolumeChanged(getCurrentVolume());
    }
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public interface Callback {
    void onVolumeChanged(int volume);
  }


  private class VolumeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (VOLUME_CHANGED_ACTION.equals(intent.getAction()) &&
        (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)) {
        volumeChanged();
        Log.d(TAG, "volumeChanged=" + this.hashCode());
      }
    }
  }

}
