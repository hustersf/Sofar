package com.sofar.utility.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;

public class KeyBoardOpenHelper implements ViewTreeObserver.OnGlobalLayoutListener {

  private static final int HEIGHT_DIFF = 200;

  OnSoftKeyboardChangeListener listener;
  View rootView;
  int rootViewVisibleHeight;//记录根视图的显示高度

  @Override
  public void onGlobalLayout() {
    if (rootView == null) {
      return;
    }

    //获取当前根视图在屏幕上显示的大小
    Rect r = new Rect();
    rootView.getWindowVisibleDisplayFrame(r);
    int visibleHeight = r.height();
    if (rootViewVisibleHeight == 0) {
      rootViewVisibleHeight = visibleHeight;
      return;
    }
    //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
    if (rootViewVisibleHeight == visibleHeight) {
      return;
    }

    //根视图显示高度变小超过临界值，可以看作软键盘显示了
    if (rootViewVisibleHeight - visibleHeight > HEIGHT_DIFF) {
      if (listener != null) {
        listener.keyboardShow(rootViewVisibleHeight - visibleHeight);
      }
      rootViewVisibleHeight = visibleHeight;
      return;
    }

    //根视图显示高度变大超过临界值，可以看作软键盘隐藏了
    if (visibleHeight - rootViewVisibleHeight > HEIGHT_DIFF) {
      if (listener != null) {
        listener.keyboardHide(visibleHeight - rootViewVisibleHeight);
      }
      rootViewVisibleHeight = visibleHeight;
      return;
    }
  }

  public void register(@NonNull Activity activity, OnSoftKeyboardChangeListener listener) {
    this.listener = listener;
    rootView = activity.getWindow().getDecorView();
    rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  public void unregister() {
    if (rootView != null) {
      rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    rootView = null;
    listener = null;
  }

  public interface OnSoftKeyboardChangeListener {
    void keyboardShow(int height);

    void keyboardHide(int height);
  }
}
