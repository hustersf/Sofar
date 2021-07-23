package com.sofar.preloader.state;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.worker.Worker;

/**
 * DataListener is listening for data
 */
public class StateListening extends StateBase {

  public StateListening(Worker<?> worker) {
    super(worker);
  }

  @Override
  public boolean dataLoadFinished() {
    super.dataLoadFinished();
    return worker.doSendLoadedDataToListenerWork();
  }

  @Override
  public boolean listenData(DataListener listener) {
    super.listenData(listener);
    return worker.doAddListenerWork(listener);
  }

  @Override
  public boolean destroy() {
    return worker.doDestroyWork();
  }

  @Override
  public String name() {
    return "StateListening";
  }

}
