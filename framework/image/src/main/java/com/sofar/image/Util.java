package com.sofar.image;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public class Util {

  /**
   * SD卡是否可用
   */
  public static boolean isSDCardEnable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  /**
   * 得到手机的缓存目录
   */
  public static File getCacheDir(@NonNull Context context) {
    // 获取保存的文件夹路径
    File file;
    if (isSDCardEnable()) {
      // 有SD卡就保存到sd卡
      file = context.getExternalCacheDir();
    } else {
      // 没有就保存到内部储存
      file = context.getCacheDir();
    }
    return file;
  }

}
