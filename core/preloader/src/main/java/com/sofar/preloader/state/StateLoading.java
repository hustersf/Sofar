package com.sofar.preloader.state;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.worker.Worker;

/**
 * state for loading data
 */
public class StateLoading extends StateBase {

  public StateLoading(Worker<?> worker) {
    super(worker);
  }

  @Override
  public boolean dataLoadFinished() {
    super.dataLoadFinished();
    return worker.doDataLoadFinishWork();
  }

  @Override
  public boolean listenData() {
    super.listenData();
    return worker.doWaitForDataLoaderWork();
  }


  @Override
  public boolean listenData(DataListener listener) {
    super.listenData(listener);
    return worker.doWaitForDataLoaderWork(listener);
  }

  @Override
  public boolean destroy() {
    return worker.doDestroyWork();
  }

  @Override
  public String name() {
    return "StateLoading";
  }

}
