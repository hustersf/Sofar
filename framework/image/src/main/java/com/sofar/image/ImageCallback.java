package com.sofar.image;

import android.graphics.Bitmap;
import androidx.annotation.MainThread;

public interface ImageCallback {

  @MainThread
  void onSuccess(Bitmap bitmap);
}
