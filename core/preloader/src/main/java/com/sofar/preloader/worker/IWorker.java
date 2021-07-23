package com.sofar.preloader.worker;

import com.sofar.preloader.interfaces.DataListener;

/**
 * interface data loader worker
 */
public interface IWorker {

  /**
   * start to load data
   */
  boolean preLoad();

  /**
   * refresh worker
   */
  boolean refresh();


  /**
   * refresh worker
   */
  default boolean refresh(String key) {
    return refresh();
  }

  /**
   * start to listen data with {@link DataListener}
   *
   * @param dataListener {@link DataListener}
   */
  boolean listenData(DataListener dataListener);

  /**
   * start to listen data with no {@link DataListener}
   * you can add {@link DataListener} later
   */
  boolean listenData();

  /**
   * remove {@link DataListener} for worker
   *
   * @param listener {@link DataListener}
   */
  boolean removeListener(DataListener listener);

  /**
   * destroy this worker
   */
  boolean destroy();
}
