package com.sofar.preferences;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sofar.preferences.model.MMKVCheckError;
import com.sofar.preferences.model.MMKVTrimMessage;
import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;
import com.tencent.mmkv.MMKVRecoverStrategic;

public class SofarSharedPreferences {

  private static final String TAG = "SofarSharedPreferences";

  private static final Map<String, SharedPreferences> CACHE = new ConcurrentHashMap<>();

  /**
   * 回滚标志位，部分低端机加载so失败时回滚为原生sp组件
   */
  private static volatile boolean sUseMMKV = true;

  /**
   * 单个文件超过4M需要trim并上报，防止内存占用过多
   */
  private static final long TRIM_LIMIT = 4 * 1024 * 1024;

  private static final String KEY = "Dw@5hhdf$70Ac4gh";

  static {
    try {
      initMMKV();
    } catch (UnsatisfiedLinkError e) {
      // 加载mmkv so失败，降级使用sp，上报
      sUseMMKV = false;
      PreferenceConfigHolder.CONFIG.logEvent("mmkv init error", e.toString());
    }
  }

  public static SharedPreferences obtain(@NonNull Context context, @NonNull String name, int mode) {
    SharedPreferences source = CACHE.get(name);
    if (source != null) {
      return source;
    }

    synchronized (name.intern()) {
      Log.d(TAG, "first obtain " + name + " in " + (sUseMMKV ? "mmkv" : "sp") + " mode.");
      if (!sUseMMKV) {
        //降级使用sp
        source = context.getSharedPreferences(name, mode);
        CACHE.put(name, source);
        return source;
      }

      //使用mmkv需要迁移sp中的数据
      File mmkvFile = new File(MMKV.getRootDir(), name);
      boolean needImport = false;
      if (!mmkvFile.exists()) {
        needImport = true;
      } else {
        // 如果sp的lastModified时间大于mmkv的时间戳，说明之前有回滚，需要同步到mmkv
        File spFile = new File(PreferenceConfigHolder.CONFIG.getSharedPreferencesRoot(),
          name + ".xml");
        if (spFile.exists() && spFile.lastModified() > mmkvFile.lastModified()) {
          needImport = true;
        }
      }

      MMKV target = MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE, KEY);
      if (needImport) {
        Log.d(TAG, "import data from " + name + ".xml");
        source = context.getSharedPreferences(name, mode);
        target.importFromSharedPreferences(source);
      }
      CACHE.put(name, target);
      trim(name, target);
      return target;
    }
  }

  private static void trim(String name, MMKV target) {
    long beforeTrim = target.totalSize();
    if (beforeTrim > TRIM_LIMIT) {
      target.trim();
      long afterTrim = target.totalSize();
      MMKVTrimMessage message = new MMKVTrimMessage();
      message.beforeTrimKb = beforeTrim / 1024;
      message.afterTrimKb = afterTrim / 1024;
      message.file = name;
      message.processName = PreferenceConfigHolder.CONFIG.getProcessName();
      message.stackTrace = Log.getStackTraceString(new Throwable());
      String[] keys = target.allKeys();
      message.valueSizeMap = new HashMap<>();
      if (keys != null) {
        for (String key : keys) {
          message.valueSizeMap.put(key, target.getValueSize(key));
        }
      }

      Log.d(TAG, "trim beforeTrimKb=" + message.beforeTrimKb
        + " afterTrimKb=" + message.afterTrimKb);
      PreferenceConfigHolder.CONFIG.logEvent("mmkv_trim",
        PreferenceConfigHolder.CONFIG.getGson().toJson(message));
    }
  }

  private static void initMMKV() {
    MMKV.initialize(PreferenceConfigHolder.CONFIG.getContext(),
      libName -> PreferenceConfigHolder.CONFIG.loadLibrary(libName));

    MMKVHandler handler = new MMKVHandler() {
      @Override
      public MMKVRecoverStrategic onMMKVCRCCheckFail(String mmapID) {
        reportMMKVCheckError(mmapID, "CRCCheckFail");
        return MMKVRecoverStrategic.OnErrorDiscard;
      }

      @Override
      public MMKVRecoverStrategic onMMKVFileLengthError(String mmapID) {
        reportMMKVCheckError(mmapID, "FileLengthError");
        return MMKVRecoverStrategic.OnErrorDiscard;
      }

      @Override
      public boolean wantLogRedirecting() {
        return false;
      }

      @Override
      public void mmkvLog(MMKVLogLevel level, String file, int line, String function,
        String message) {
        switch (level) {
          case LevelDebug:
            Log.d(TAG, function + " == " + message);
            break;
          case LevelWarning:
            Log.w(TAG, function + " == " + message);
            break;
          case LevelError:
            Log.e(TAG, function + " == " + message);
          case LevelNone:
          default:
            break;
          case LevelInfo:
            Log.i(TAG, function + " == " + message);
        }
      }
    };
    MMKV.registerHandler(handler);
  }

  private static void reportMMKVCheckError(String mmapID, String type) {
    MMKVCheckError error = new MMKVCheckError();
    error.file = mmapID;
    error.type = type;

    PreferenceConfigHolder.CONFIG.logEvent("mmkv_check_error",
      PreferenceConfigHolder.CONFIG.getGson().toJson(error));
  }


  public static Set<String> getKeySet(SharedPreferences sp) {
    if (sp instanceof MMKV) {
      String[] keys = ((MMKV) sp).allKeys();
      if (keys == null || keys.length == 0) {
        return Collections.emptySet();
      }
      Set<String> set = new HashSet<>(keys.length);
      Collections.addAll(set, keys);
      return set;
    } else {
      return sp.getAll().keySet();
    }
  }

}
