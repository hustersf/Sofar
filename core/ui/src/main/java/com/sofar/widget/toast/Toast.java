package com.sofar.widget.toast;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sofar.utility.reflect.FieldUtil;

/**
 * 继承自系统 Toast
 * 目的解决系统 Toast 存在的问题
 */
public class Toast extends android.widget.Toast {

  private static final String TAG = "HookToast";

  public Toast(Context context) {
    super(context);
  }

  @Override
  public void show() {
    if (checkIfNeedToHook()) {
      tryHook();
    }
    super.show();
  }

  /**
   * 7.1 toast bad token
   */
  private void tryHook() {
    try {
      final Object tn = FieldUtil.readField(this, "mTN");
      if (tn == null) {
        Log.w(TAG, "Field mTN of " + this + " is null");
        return;
      }

      final Object handler = FieldUtil.readField(tn, "mHandler");
      if (handler instanceof Handler) {
        FieldUtil.writeField(handler, "mCallback", new CaughtCallback((Handler) handler));
      }
    } catch (Exception e) {

    }
  }

  private boolean checkIfNeedToHook() {
    return Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1;
  }

  private static class CaughtCallback implements Handler.Callback {
    private final Handler mHandler;

    CaughtCallback(Handler mHandler) {
      this.mHandler = mHandler;
    }

    @Override
    public boolean handleMessage(Message msg) {
      try {
        mHandler.handleMessage(msg);
      } catch (Throwable e) {
        e.printStackTrace();
      }
      return true;
    }
  }

}
