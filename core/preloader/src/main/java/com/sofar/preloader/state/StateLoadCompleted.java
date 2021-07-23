package com.sofar.preloader.state;

import com.sofar.preloader.worker.Worker;
import com.sofar.preloader.interfaces.DataListener;

/**
 * data load finished, waiting for {@link DataListener}
 */
public class StateLoadCompleted extends StateBase {

  public StateLoadCompleted(Worker<?> worker) {
    super(worker);
  }

  @Override
  public boolean refresh() {
    super.refresh();
    return worker.doStartLoadWork();
  }

  @Override
  public boolean listenData() {
    super.listenData();
    return worker.doSendLoadedDataToListenerWork();
  }

  @Override
  public boolean listenData(DataListener listener) {
    super.listenData(listener);
    return worker.doSendLoadedDataToListenerWork(listener);
  }

  @Override
  public boolean destroy() {
    return worker.doDestroyWork();
  }

  @Override
  public String name() {
    return "StateLoadCompleted";
  }

}
