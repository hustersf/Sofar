package com.sofar.download;

/**
 * 下载请求对应的状态
 * {@link DownloadListener 一一对应}
 */
public @interface DownloadStatus {

  int INIT = 0; //初始状态

  int CONNECTED = 1; //连接状态

  int WAIT = 2;  //等待状态

  int CANCELED = 3;  //取消状态

  int COMPLETE = 4;  //完成状态

  int FAILED = 5;  //失败状态

  int PROGRESS = 6;  //下载中

}
