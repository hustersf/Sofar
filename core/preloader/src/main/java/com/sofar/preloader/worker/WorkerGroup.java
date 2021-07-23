package com.sofar.preloader.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.text.TextUtils;

import com.sofar.preloader.Logger;
import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.interfaces.GroupedDataListener;
import com.sofar.preloader.interfaces.GroupedDataLoader;

public class WorkerGroup implements IWorker {

  private Collection<Worker> workers;

  public WorkerGroup(GroupedDataLoader[] loaders) {
    if (loaders != null) {
      HashMap<String, Worker> map = new HashMap<>(loaders.length);
      for (GroupedDataLoader loader : loaders) {
        if (loader == null) {
          continue;
        }
        String key = loader.keyInGroup();
        if (TextUtils.isEmpty(key)) {
          Logger.warning("GroupedDataLoader with no key:"
            + loader.getClass().getName());
        }
        Worker old = map.put(key, new Worker(loader, (DataListener) null));
        if (old != null) {
          Logger.error("More than 1 loaders with same key:("
            + loader.getClass().getName()
            + ", " + old.getClass().getName()
            + "). " + old.getClass().getName() + " will be skipped.");
        }
      }
      this.workers = map.values();
    } else {
      this.workers = new ArrayList<>();
    }
  }

  @Override
  public boolean preLoad() {
    boolean success = true;
    for (Worker worker : workers) {
      success &= worker.preLoad();
    }
    return success;
  }

  @Override
  public boolean refresh() {
    boolean success = true;
    for (Worker worker : workers) {
      success &= worker.refresh();
    }
    return success;
  }

  @Override
  public boolean refresh(String key) {
    boolean success = false;
    for (Worker worker : workers) {
      if (worker != null && TextUtils.equals(worker.getKey(), key)) {
        success = worker.refresh();
      }
    }
    return success;
  }

  @Override
  public boolean listenData(DataListener dataListener) {
    boolean success = true;
    String key = null;
    if (dataListener != null && dataListener instanceof GroupedDataListener) {
      key = ((GroupedDataListener) dataListener).keyInGroup();
    }
    for (Worker worker : workers) {
      if (!TextUtils.isEmpty(key) && worker.dataLoader instanceof GroupedDataLoader) {
        GroupedDataLoader loader = (GroupedDataLoader) worker.dataLoader;
        if (key.equals(loader.keyInGroup())) {
          success &= worker.listenData(dataListener);
        }
      }
    }
    return success;
  }

  @Override
  public boolean listenData() {
    boolean success = true;
    for (Worker worker : workers) {
      success &= worker.listenData();
    }
    return success;
  }

  @Override
  public boolean removeListener(DataListener listener) {
    boolean success = true;
    for (Worker worker : workers) {
      success &= worker.removeListener(listener);
    }
    return success;
  }

  @Override
  public boolean destroy() {
    boolean success = true;
    for (Worker worker : workers) {
      success &= worker.destroy();
    }
    workers.clear();
    return success;
  }
}
