package com.sofar.preloader;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.interfaces.DataLoader;
import com.sofar.preloader.interfaces.GroupedDataListener;
import com.sofar.preloader.interfaces.GroupedDataLoader;
import com.sofar.preloader.worker.IWorker;
import com.sofar.preloader.worker.Worker;
import com.sofar.preloader.worker.WorkerGroup;

public class PreLoaderPool {

  private static class Inner {
    static final PreLoaderPool INSTANCE = new PreLoaderPool();
  }

  public static PreLoaderPool getDefault() {
    return Inner.INSTANCE;
  }

  private final AtomicLong idMaker = new AtomicLong(0);

  private final ConcurrentHashMap<Long, IWorker> workerMap = new ConcurrentHashMap<>();

  public <T> long preLoad(DataLoader<T> loader) {
    Worker<T> worker = new Worker<>(loader, (DataListener<T>) null);
    return preLoadWorker(worker);
  }

  public <T> long preLoad(DataLoader<T> loader, DataListener<T> listener) {
    Worker<T> worker = new Worker<>(loader, listener);
    return preLoadWorker(worker);
  }

  public <T> long preLoad(DataLoader<T> loader, List<DataListener<T>> listeners) {
    Worker<T> worker = new Worker<>(loader, listeners);
    return preLoadWorker(worker);
  }

  private <T> long preLoadWorker(Worker<T> worker) {
    long id = idMaker.incrementAndGet();
    workerMap.put(id, worker);
    worker.preLoad();
    return id;
  }

  public long preLoadGroup(GroupedDataLoader... loaders) {
    long id = idMaker.incrementAndGet();
    WorkerGroup group = new WorkerGroup(loaders);
    workerMap.put(id, group);
    group.preLoad();
    return id;
  }

  public boolean exists(int id) {
    return workerMap.containsKey(id);
  }


  public boolean listenData(int id) {
    IWorker worker = workerMap.get(id);
    return worker != null && worker.listenData();
  }

  public <T> boolean listenData(int id, DataListener<T> dataListener) {
    IWorker worker = workerMap.get(id);
    return worker != null && worker.listenData(dataListener);
  }

  public boolean listenData(int id, GroupedDataListener... listeners) {
    IWorker worker = workerMap.get(id);
    if (worker != null) {
      for (GroupedDataListener listener : listeners) {
        worker.listenData(listener);
      }
    }
    return true;
  }

  public <T> boolean removeListener(int id, DataListener<T> dataListener) {
    IWorker worker = workerMap.get(id);
    return worker != null && worker.removeListener(dataListener);
  }

  public boolean refresh(int id) {
    IWorker worker = workerMap.get(id);
    return worker != null && worker.refresh();
  }

  public boolean refresh(int id, String key) {
    IWorker worker = workerMap.get(id);
    return worker != null && worker.refresh(key);
  }

  public boolean destroy(int id) {
    IWorker worker = workerMap.remove(id);
    return worker != null && worker.destroy();
  }

  public boolean destroyAll() {
    for (IWorker worker : workerMap.values()) {
      if (worker != null) {
        worker.destroy();
      }
    }
    workerMap.clear();
    idMaker.set(0);
    return true;
  }

}
