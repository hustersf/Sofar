package com.sofar.preloader;


import java.util.List;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.interfaces.DataLoader;
import com.sofar.preloader.worker.Worker;

/**
 * single pre-loader wrapper
 */
public class PreLoaderWrapper<T> {

  private Worker<T> worker;

  PreLoaderWrapper(DataLoader<T> loader, DataListener<T> listener) {
    this.worker = new Worker<>(loader, listener);
  }

  PreLoaderWrapper(DataLoader<T> loader, List<DataListener<T>> listeners) {
    this.worker = new Worker<>(loader, listeners);
  }

  boolean preLoad() {
    return this.worker.preLoad();
  }

  /**
   * start to listen data with dataListener of worker
   */
  public boolean listenData() {
    return worker.listenData();
  }

  /**
   * start to listen data with this dataListener
   */
  public boolean listenData(DataListener<T> dataListener) {
    return worker.listenData(dataListener);
  }

  public boolean removeListener(DataListener<T> dataListener) {
    return worker.removeListener(dataListener);
  }

  /**
   * re-load data for all listeners
   *
   * @return success
   */
  public boolean refresh() {
    return worker.refresh();
  }

  public boolean destroy() {
    return worker.destroy();
  }
}
