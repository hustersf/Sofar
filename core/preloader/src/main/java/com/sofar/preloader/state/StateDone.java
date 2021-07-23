package com.sofar.preloader.state;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.worker.Worker;

/**
 * data load finished, and send data to{@link DataListener}
 */
public class StateDone extends StateBase {

  public StateDone(Worker<?> worker) {
    super(worker);
  }

  @Override
  public boolean refresh() {
    super.refresh();
    return worker.doStartLoadWork() && worker.listenData();
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
    return "StateDone";
  }

}
