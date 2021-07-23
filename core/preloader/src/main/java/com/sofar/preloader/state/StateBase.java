package com.sofar.preloader.state;

import android.util.Log;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.worker.Worker;

/**
 * basic state
 */
public abstract class StateBase implements State {

  protected Worker<?> worker;

  public StateBase(Worker<?> worker) {
    this.worker = worker;
  }

  @Override
  public boolean startLoad() {
    log("startLoad()");
    return false;
  }

  @Override
  public boolean destroy() {
    log("destroy()");
    return false;
  }

  @Override
  public boolean listenData() {
    log("listenData()");
    return false;
  }

  @Override
  public boolean listenData(DataListener listener) {
    log("listenData(listener)");
    return false;
  }

  @Override
  public boolean removeListener(DataListener listener) {
    log("removeListener(listener)");
    return worker.doRemoveListenerWork(listener);
  }

  @Override
  public boolean dataLoadFinished() {
    log("dataLoadFinished()");
    return false;
  }

  @Override
  public boolean refresh() {
    log("refresh()");
    return false;
  }

  private void log(String str) {
    Log.d(name(), "--->>> " + str);
  }

}
