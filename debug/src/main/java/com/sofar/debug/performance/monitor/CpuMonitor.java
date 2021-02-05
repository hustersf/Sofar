package com.sofar.debug.performance.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.sofar.debug.performance.PerformanceMonitorManager;

/**
 * 监控cpu使用率
 */
public class CpuMonitor extends AbsMonitor {

  private static final String TAG = "CpuMonitor";

  private static final int CPU_SAMPLING_TIME = 1000;

  private RandomAccessFile mProcStatFile;
  private RandomAccessFile mAppStatFile;
  private Long mLastCpuTime;
  private Long mLastAppCpuTime;

  private float mCpuRate;

  private Runnable cpuRateRunnable = new Runnable() {
    @Override
    public void run() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        mCpuRate = getCpuDataForO();
      } else {
        mCpuRate = getCPUData();
      }
      record();
      handler.postDelayed(this, CPU_SAMPLING_TIME);
    }
  };


  @Override
  protected void onStart() {
    handler.postDelayed(cpuRateRunnable, CPU_SAMPLING_TIME);
  }

  @Override
  protected void onStop() {
    handler.removeCallbacks(cpuRateRunnable);
  }

  private void record() {
    Log.d(TAG, "cpu使用率=" + mCpuRate);
    PerformanceMonitorManager.get().cpuCallback(mCpuRate);
  }

  private float getCpuDataForO() {
    java.lang.Process process = null;
    try {
      process = Runtime.getRuntime().exec("top -n 1");
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      int cpuIndex = -1;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (TextUtils.isEmpty(line)) {
          continue;
        }
        int tempIndex = getCPUIndex(line);
        if (tempIndex != -1) {
          cpuIndex = tempIndex;
          continue;
        }
        if (line.startsWith(String.valueOf(Process.myPid()))) {
          if (cpuIndex == -1) {
            continue;
          }
          String[] param = line.split("\\s+");
          if (param.length <= cpuIndex) {
            continue;
          }
          String cpu = param[cpuIndex];
          if (cpu.endsWith("%")) {
            cpu = cpu.substring(0, cpu.lastIndexOf("%"));
          }
          float rate = Float.parseFloat(cpu) / Runtime.getRuntime().availableProcessors();
          return rate;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (process != null) {
        process.destroy();
      }
    }
    return 0;
  }

  private float getCPUData() {
    long cpuTime;
    long appTime;
    float value = 0.0f;
    try {
      if (mProcStatFile == null || mAppStatFile == null) {
        mProcStatFile = new RandomAccessFile("/proc/stat", "r");
        mAppStatFile = new RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r");
      } else {
        mProcStatFile.seek(0L);
        mAppStatFile.seek(0L);
      }
      String procStatString = mProcStatFile.readLine();
      String appStatString = mAppStatFile.readLine();
      String procStats[] = procStatString.split(" ");
      String appStats[] = appStatString.split(" ");
      cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
        + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
        + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
        + Long.parseLong(procStats[8]);
      appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
      if (mLastCpuTime == null && mLastAppCpuTime == null) {
        mLastCpuTime = cpuTime;
        mLastAppCpuTime = appTime;
        return value;
      }
      value = ((float) (appTime - mLastAppCpuTime) / (float) (cpuTime - mLastCpuTime)) * 100f;
      mLastCpuTime = cpuTime;
      mLastAppCpuTime = appTime;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return value;
  }

  private int getCPUIndex(String line) {
    if (line.contains("CPU")) {
      String[] titles = line.split("\\s+");
      for (int i = 0; i < titles.length; i++) {
        if (titles[i].contains("CPU")) {
          return i;
        }
      }
    }
    return -1;
  }
}
