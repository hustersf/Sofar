package com.sofar.debug.performance.monitor;

public interface MonitorCallback {

  /**
   * @param frameRate 帧率
   */
  default void onFrameRate(int frameRate) {}

  /**
   * @param cpuRate cpu使用率
   */
  default void onCpuRate(float cpuRate) {}


  /**
   * @param count 线程数
   */
  default void onThreadCount(int count) {}

  /**
   * @param size 当前使用内存大小，单位MB
   */
  default void onMemory(float size) {}

  /**
   * @param count fd数量
   */
  default void onFDCount(int count) {}
}
