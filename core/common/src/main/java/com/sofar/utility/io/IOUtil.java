package com.sofar.utility.io;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {

  public static final int EOF = -1;
  public static final int DEFAULT_BUFFER_SIZE = 8192;

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
