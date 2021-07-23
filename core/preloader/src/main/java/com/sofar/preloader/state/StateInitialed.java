package com.sofar.preloader.state;

import com.sofar.preloader.worker.Worker;

/**
 * initial state
 */
public class StateInitialed extends StateBase {

  public StateInitialed(Worker<?> worker) {
    super(worker);
  }

  @Override
  public boolean startLoad() {
    super.startLoad();
    return worker.doStartLoadWork();
  }

  @Override
  public boolean destroy() {
    return worker.doDestroyWork();
  }

  @Override
  public String name() {
    return "StatusInitialed";
  }

}
