package com.sofar.utility.keyboard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
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

}
