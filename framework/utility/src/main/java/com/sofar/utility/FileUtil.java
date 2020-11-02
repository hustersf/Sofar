package com.sofar.utility;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件工具类
 */
public class FileUtil {

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

  /**
   * 得到手机的文件目录
   */
  public static File getFileDir(@NonNull Context context) {
    // 获取保存的文件夹路径
    File file;
    if (isSDCardEnable()) {
      // 有SD卡就保存到sd卡
      file = context.getExternalFilesDir(null);
    } else {
      // 没有就保存到内部储存
      file = context.getFilesDir();
    }
    return file;
  }

  /**
   * 写文件
   */
  public static void writeToFile(@NonNull File dstFile, @NonNull InputStream dataSource) {
    FileOutputStream fos = null;
    try {
      if (!dstFile.exists()) {
        dstFile.createNewFile();
      }
      fos = new FileOutputStream(dstFile);
      byte[] buffer = new byte[1024];
      int len;
      while ((len = dataSource.read(buffer)) != -1) {
        fos.write(buffer, 0, len);
        fos.flush();
      }
      fos.close();
      dataSource.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeQuietly(fos);
      closeQuietly(dataSource);
    }
  }

  /**
   * 读取文本文件
   */
  public static String getTextFromFile(@NonNull File file) {
    StringBuffer sb = new StringBuffer();
    FileReader reader = null;
    BufferedReader br = null;
    try {
      reader = new FileReader(file);
      br = new BufferedReader(reader);
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
      br.close();
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      closeQuietly(reader);
      closeQuietly(br);
    }
    return sb.toString();
  }

  /**
   * 从assets目录下读取文本文件
   */
  public static String getTextFromAssets(@NonNull Context context, String fileName) {
    String result = "";
    InputStream is = null;
    try {
      is = context.getAssets().open(fileName);
      byte[] buffer = new byte[is.available()];
      is.read(buffer);
      result = new String(buffer, "utf-8");
      is.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeQuietly(is);
    }
    return result;
  }

  /**
   * 关闭io流
   */
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
