package com.sofar.skin.callback;

/**
 * 用来添加、删除需要皮肤更新的界面以及通知界面皮肤更新
 */
public interface ISkinObserver {

  void attach(ISkinUpdate observer);

  void detach(ISkinUpdate observer);

  void notifySkinUpdate();
}
