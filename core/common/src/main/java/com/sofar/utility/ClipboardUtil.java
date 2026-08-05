package com.sofar.utility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * 系统剪贴板
 */
public class ClipboardUtil {


  /**
   * 将给定content复制到系统粘贴板中
   */
  public static void copy(String content, @NonNull Context context) {
    // 得到剪贴板管理器
    ClipboardManager cbm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData data = ClipData.newPlainText("", content);
    cbm.setPrimaryClip(data);
  }

  /**
   * 粘贴
   */
  public static String paste(@NonNull Context context) {
    // 得到剪贴板管理器
    ClipboardManager cbm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData data = cbm.getPrimaryClip();
    if (data == null || data.getItemCount() == 0) {
      return "";
    }

    ClipData.Item item = data.getItemAt(0);
    if (TextUtils.isEmpty(item.getText())) {
      return "";
    }
    return item.getText().toString();
  }
}
