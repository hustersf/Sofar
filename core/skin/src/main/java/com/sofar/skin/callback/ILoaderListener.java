package com.sofar.skin.callback;

/**
 * 加载皮肤包监听
 */
public interface ILoaderListener {

  void onStart();

  void onSuccess();

  void onFailed(String errMsg);

  /**
   * 当从网络上加载皮肤文件是此方法会被调用
   *
   * @param progress 下载进度
   */
  void onProgress(int progress);
}
