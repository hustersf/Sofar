package com.sofar.base;

import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sofar.base.util.WeakHandler;

import io.reactivex.annotations.NonNull;

/**
 * 通过将EmptyView add 到父View
 * 可检测View的可见/不可见，展示时长等等
 */
public class EmptyView extends View implements WeakHandler.MessageCallback {

  private static final String TAG = "EmptyView";

  private ViewCallback viewCallback;

  private WeakHandler handler = new WeakHandler(this, Looper.getMainLooper());
  private static final int MSG_WHAT = 1;

  private boolean attach;
  private Rect rect = new Rect();
  private long start;

  public EmptyView(Context context) {
    super(context);
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    setLayoutParams(new ViewGroup.LayoutParams(width, height));
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Log.d(TAG, "onAttachedToWindow");
    attach();
    if (viewCallback != null) {
      viewCallback.onViewAttached();
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Log.d(TAG, "onDetachedFromWindow");
    detach();
    if (viewCallback != null) {
      viewCallback.onViewDetached();
    }
  }


  @Override
  public void onWindowFocusChanged(boolean hasWindowFocus) {
    super.onWindowFocusChanged(hasWindowFocus);
    Log.d(TAG, "onWindowFocusChanged:" + hasWindowFocus);
  }

  @Override
  public void handleMsg(@NonNull Message msg) {
    boolean visible = checkVisible();
    adShowDuration(visible);
    handler.sendEmptyMessageDelayed(MSG_WHAT, 1000);
  }

  private void attach() {
    if (attach) {
      return;
    }

    attach = true;
    handler.sendEmptyMessage(MSG_WHAT);
  }

  private void detach() {
    if (!attach) {
      return;
    }

    attach = false;
    handler.removeCallbacksAndMessages(null);
    adShowDuration(false);
  }

  private void adShowDuration(boolean visible) {
    if (visible && start <= 0) {
      start = SystemClock.elapsedRealtime();
      if (viewCallback != null) {
        viewCallback.onViewShow();
      }
    }

    boolean ret = !visible || !attach;
    if (ret && start > 0) {
      if (viewCallback != null) {
        viewCallback.onViewDismiss();
      }
      long duration = SystemClock.elapsedRealtime() - start;
      start = 0;

      if (duration > 0) {
        if (viewCallback != null) {
          viewCallback.onViewShowDuration(duration);
        }
      }
    }
  }

  private boolean checkVisible() {
    if (!isShown() || !hasWindowFocus()) {
      return false;
    }

    return getGlobalVisibleRect(rect);
  }

  public void setViewCallback(ViewCallback callback) {
    this.viewCallback = callback;
  }

  public interface ViewCallback {
    void onViewAttached();

    void onViewDetached();

    void onViewShow();

    void onViewDismiss();

    void onViewShowDuration(long duration);
  }
}
