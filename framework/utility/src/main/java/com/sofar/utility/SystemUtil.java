package com.sofar.utility;

import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SystemUtil {

  private static volatile String sProcessName;
  private static volatile Boolean sIsMainProcess;

  /**
   * 当前进程是否是主进程
   */
  public static boolean isInMainProcess(@NonNull Context context) {
    if (sIsMainProcess == null) {
      String pName = getProcessName(context);
      sIsMainProcess = !TextUtils.isEmpty(pName) && pName.equals(context.getPackageName());
    }
    return sIsMainProcess;
  }

  /**
   * 获取当前进程名字
   */
  @Nullable
  public static String getProcessName(@NonNull Context context) {
    if (!TextUtils.isEmpty(sProcessName)) {
      return sProcessName;
    }

    sProcessName = getCurrentProcessNameByApplication();
    if (!TextUtils.isEmpty(sProcessName)) {
      return sProcessName;
    }

    sProcessName = getCurrentProcessNameByActivityThread();
    if (!TextUtils.isEmpty(sProcessName)) {
      return sProcessName;
    }

    sProcessName = getCurrentProcessNameByActivityManager(context);
    return sProcessName;
  }

  /**
   * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
   */
  @Nullable
  public static String getCurrentProcessNameByApplication() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      return Application.getProcessName();
    }
    return null;
  }

  /**
   * 通过反射ActivityThread获取进程名，避免了ipc
   */
  @Nullable
  public static String getCurrentProcessNameByActivityThread() {
    String processName = null;
    try {
      final Method declaredMethod = Class.forName("android.app.ActivityThread", false,
        Application.class.getClassLoader()).getDeclaredMethod("currentProcessName", new Class[0]);
      declaredMethod.setAccessible(true);
      final Object invoke = declaredMethod.invoke(null, new Object[0]);
      if (invoke instanceof String) {
        processName = (String) invoke;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return processName;
  }

  /**
   * 通过ActivityManager 获取进程名，需要IPC通信
   */
  @Nullable
  public static String getCurrentProcessNameByActivityManager(@NonNull Context context) {
    int pid = Process.myPid();
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (am != null) {
      List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
      if (runningAppList != null) {
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppList) {
          if (processInfo.pid == pid) {
            return processInfo.processName;
          }
        }
      }
    }
    return null;
  }

}
