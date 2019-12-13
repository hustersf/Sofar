package com.sofar.skin.util;

import android.content.Context;
import android.os.Environment;

import com.sofar.skin.config.SkinConfig;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 皮肤文件管理
 */
public class SkinFileUtil {

  /**
   * 复制assets/skin目录下的皮肤文件到指定目录
   */
  public static void copySkinAssetsToDir(Context context, String skinName, String toDir) {
    InputStream is = null;
    OutputStream os = null;
    try {
      is = context.getAssets().open(SkinConfig.SKIN_DIR_NAME + File.separator + skinName);

      File fileDir = new File(toDir);
      if (!fileDir.exists()) {
        fileDir.mkdirs();
      }

      String toFile = toDir + File.separator + skinName;
      os = new FileOutputStream(toFile);
      int byteCount;
      byte[] bytes = new byte[1024];

      while ((byteCount = is.read(bytes)) != -1) {
        os.write(bytes, 0, byteCount);
      }
      os.close();
      is.close();
    } catch (IOException e) {
      SkinL.d("copySkinAssetsToDir failed:" + skinName);
    } finally {
      closeQuietly(os);
      closeQuietly(is);
    }
  }

  /**
   * 得到存放皮肤的目录
   */
  public static String getSkinDir(Context context) {
    File skinDir = new File(getCacheDir(context), SkinConfig.SKIN_DIR_NAME);
    if (!skinDir.exists()) {
      skinDir.mkdirs();
    }
    return skinDir.getAbsolutePath();
  }


  /**
   * 得到手机的缓存目录
   */
  private static String getCacheDir(Context context) {
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      File cacheDir = context.getExternalCacheDir();
      return cacheDir.getAbsolutePath();
    } else {
      File cacheDir = context.getCacheDir();
      return cacheDir.getAbsolutePath();
    }
  }

  public static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException ioe) {
      // ignore
    }
  }

}
