package com.sofar.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

/**
 * 键盘工具类
 */
public class KeyboardUtil {

  /**
   * 关闭键盘
   */
  public static void hideKeyboard(@NonNull View view) {
    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  /**
   * 关闭键盘
   */
  public static void hideKeyboard(@NonNull Activity activity) {
    View focus = activity.getCurrentFocus();
    if (focus != null) {
      hideKeyboard(focus);
    }
  }


  /**
   * 打开键盘
   */
  public static void openKeyboard(@NonNull final View view) {
    view.post(new Runnable() {
      @Override
      public void run() {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
      }
    });
  }

  /**
   * 检测键盘显示和隐藏，获取键盘高度
   */
  public static void setListener(@NonNull Activity activity, OnSoftKeyboardChangeListener listener) {
    new KeyboardStatusListener(activity, listener);
  }

  private static class KeyboardStatusListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private View rootView;//activity的根视图
    int rootViewVisibleHeight;//记录根视图的显示高度
    private OnSoftKeyboardChangeListener listener;

    public KeyboardStatusListener(Activity activity, OnSoftKeyboardChangeListener listener) {
      if (activity != null) {
        //获取activity的根视图
        rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.listener = listener;
      }
    }

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

      //根视图显示高度变小超过200，可以看作软键盘显示了
      if (rootViewVisibleHeight - visibleHeight > 200) {
        if (listener != null) {
          listener.keyboardShow(rootViewVisibleHeight - visibleHeight);
        }
        rootViewVisibleHeight = visibleHeight;
        return;
      }

      //根视图显示高度变大超过200，可以看作软键盘隐藏了
      if (visibleHeight - rootViewVisibleHeight > 200) {
        if (listener != null) {
          listener.keyboardHide(visibleHeight - rootViewVisibleHeight);
        }
        rootViewVisibleHeight = visibleHeight;
        return;
      }
    }
  }


  public interface OnSoftKeyboardChangeListener {
    void keyboardShow(int height);

    void keyboardHide(int height);
  }

}
