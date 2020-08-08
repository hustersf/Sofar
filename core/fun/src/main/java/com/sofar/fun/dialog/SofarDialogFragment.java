package com.sofar.fun.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.sofar.fun.R;

/**
 * 可设置 宽高，gravity，窗口透镜度等属性的dialog
 */
public class SofarDialogFragment extends DialogFragment {

  protected boolean wrapContentHeight = true;
  protected int windowContentHeight;
  protected boolean wrapContentWidth = false;
  protected int windowContentWidth;
  protected int dialogTheme = R.style.Theme_Dialog_Translucent;
  protected int windowSoftInputMode = 0;
  protected boolean dimBackground = true;
  protected int gravity = Gravity.CENTER;
  protected int animation = -1;

  private DialogInterface.OnDismissListener dismissListener;

  public SofarDialogFragment setDialogTheme(int theme) {
    this.dialogTheme = theme;
    return this;
  }

  public SofarDialogFragment setGravity(int gravity) {
    this.gravity = gravity;
    return this;
  }

  public SofarDialogFragment setWindowAnimation(int animation) {
    this.animation = animation;
    return this;
  }

  public SofarDialogFragment setWrapContentHeight(boolean wrapContentHeight) {
    this.wrapContentHeight = wrapContentHeight;
    return this;
  }

  public SofarDialogFragment setWindowContentHeight(int windowContentHeight) {
    this.windowContentHeight = windowContentHeight;
    return this;
  }

  public SofarDialogFragment setWrapContentWidth(boolean wrapContentWidth) {
    this.wrapContentWidth = wrapContentWidth;
    return this;
  }

  public SofarDialogFragment setWindowContentWidth(int windowContentWidth) {
    this.windowContentWidth = windowContentWidth;
    return this;
  }

  public SofarDialogFragment setWindowSoftInputMode(int mode) {
    this.windowSoftInputMode = mode;
    return this;
  }

  public SofarDialogFragment setDimBackgroundEnabled(boolean dim) {
    this.dimBackground = dim;
    return this;
  }

  public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
    this.dismissListener = listener;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    int style = DialogFragment.STYLE_NO_TITLE;
    setStyle(style, dialogTheme);
    return super.onCreateDialog(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Dialog dialog = getDialog();
    super.onActivityCreated(savedInstanceState);
    Window window = dialog == null ? null : dialog.getWindow();
    Activity activity = getActivity();
    if (window != null && activity != null) {
      int height = wrapContentHeight
        ? ViewGroup.LayoutParams.WRAP_CONTENT
        : windowContentHeight != 0
        ? windowContentHeight
        : ViewGroup.LayoutParams.MATCH_PARENT;

      int width = wrapContentWidth
        ? ViewGroup.LayoutParams.WRAP_CONTENT
        : windowContentWidth != 0
        ? windowContentWidth
        : ViewGroup.LayoutParams.MATCH_PARENT;
      // 如果height这里使用MATCH_PARENT，在某些机器上（如小米），会导致状态栏闪烁一下
      try {
        window.setLayout(width, height);
        window.setGravity(gravity);
        if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) != Gravity.CENTER_HORIZONTAL
          || (gravity & Gravity.VERTICAL_GRAVITY_MASK) != Gravity.CENTER_VERTICAL) {
          window.setWindowAnimations(0);
        }
        if (animation != -1) {
          window.setWindowAnimations(animation);
        }
        window.setSoftInputMode(windowSoftInputMode);
        if (!dimBackground) {
          window.setBackgroundDrawable(new ColorDrawable(0));
          window.setDimAmount(0);
        }
      } catch (Exception e) {
      }
    }
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {
    super.onDismiss(dialog);
    if (dismissListener != null) {
      dismissListener.onDismiss(dialog);
    }
  }
}
