package com.sofar.preloader.state;

import com.sofar.preloader.worker.Worker;

/**
 * destroyed state
 */
public class StateDestroyed extends StateBase {

  public StateDestroyed(Worker<?> worker) {
    super(worker);
  }

  @Override
  public String name() {
    return "StateDestroyed";
  }

}
