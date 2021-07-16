package com.sofar.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CallStackUtil {

  public static String getStack(String msg) {
    try {
      throw new Throwable(msg);
    } catch (Throwable t) {
      return collectException(t);
    }
  }

  private static String collectException(Throwable ex) {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    ex.printStackTrace(printWriter);
    Throwable cause = ex.getCause();
    while (cause != null) {
      cause.printStackTrace(printWriter);
      // 换行 每个异常栈之间换行
      printWriter.append("\r\n");
      cause = cause.getCause();
    }
    printWriter.close();
    return writer.toString();
  }
}
