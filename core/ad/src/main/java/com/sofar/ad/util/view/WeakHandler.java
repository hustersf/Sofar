package com.sofar.ad.util.view;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class WeakHandler extends Handler {

  private final WeakReference<MessageCallback> reference;

  public WeakHandler(MessageCallback callback) {
    this.reference = new WeakReference<>(callback);
  }

  public WeakHandler(MessageCallback callback, @NonNull Looper looper) {
    super(looper);
    this.reference = new WeakReference<>(callback);
  }

  @Override
  public void handleMessage(@NonNull Message msg) {
    if (reference != null) {
      MessageCallback callback = reference.get();
      if (callback != null) {
        callback.handleMsg(msg);
      }
    }
  }

  public interface MessageCallback {
    void handleMsg(Message msg);
  }
}
