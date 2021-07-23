package com.sofar.preloader.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;
import android.os.Looper;

import com.sofar.preloader.Logger;
import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.interfaces.DataLoader;
import com.sofar.preloader.interfaces.GroupedDataLoader;
import com.sofar.preloader.state.State;
import com.sofar.preloader.state.StateDestroyed;
import com.sofar.preloader.state.StateDone;
import com.sofar.preloader.state.StateInitialed;
import com.sofar.preloader.state.StateListening;
import com.sofar.preloader.state.StateLoadCompleted;
import com.sofar.preloader.state.StateLoading;

import io.reactivex.disposables.Disposable;

public class Worker<T> implements IWorker {

  private T loadedData;
  private Throwable throwable;
  private final List<DataListener<T>> dataListeners = new CopyOnWriteArrayList<>();
  private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
  protected DataLoader<T> dataLoader;
  private Disposable disposable;
  private volatile State state;
  private String key;

  public Worker(DataLoader<T> loader, DataListener<T> listener) {
    init(loader);
    if (listener != null) {
      this.dataListeners.add(listener);
    }
  }

  public Worker(DataLoader<T> loader, List<DataListener<T>> listeners) {
    init(loader);
    if (listeners != null) {
      this.dataListeners.addAll(listeners);
    }
  }

  private void init(DataLoader<T> loader) {
    this.dataLoader = loader;
    if (loader instanceof GroupedDataLoader) {
      this.key = ((GroupedDataLoader<T>) loader).keyInGroup();
    }
    setState(new StateInitialed(this));
  }

  public String getKey() {
    return key;
  }

  @Override
  public boolean preLoad() {
    return state.startLoad();
  }

  public boolean doStartLoadWork() {
    setState(new StateLoading(this));
    run();
    return true;
  }

  @Override
  public boolean refresh() {
    return state.refresh();
  }

  @Override
  public boolean listenData(DataListener dataListener) {
    return state.listenData(dataListener);
  }

  @Override
  public boolean listenData() {
    return state.listenData();
  }

  @Override
  public boolean removeListener(DataListener listener) {
    return state.removeListener(listener);
  }

  public boolean doRemoveListenerWork(DataListener<T> listener) {
    return dataListeners.remove(listener);
  }

  @Override
  public boolean destroy() {
    return state.destroy();
  }

  public boolean doDestroyWork() {
    setState(new StateDestroyed(this));
    mainThreadHandler.removeCallbacksAndMessages(null);
    dataListeners.clear();
    dataLoader = null;
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
    }
    return true;
  }

  public boolean doWaitForDataLoaderWork(DataListener<T> listener) {
    if (listener != null) {
      dataListeners.add(listener);
    }
    return doWaitForDataLoaderWork();
  }

  public boolean doWaitForDataLoaderWork() {
    setState(new StateListening(this));
    return true;
  }

  public boolean doAddListenerWork(DataListener<T> listener) {
    if (listener != null) {
      if (!this.dataListeners.contains(listener)) {
        this.dataListeners.add(listener);
      }
      return true;
    }
    return false;
  }

  public boolean doDataLoadFinishWork() {
    setState(new StateLoadCompleted(this));
    return true;
  }

  public boolean doSendLoadedDataToListenerWork() {
    return doSendLoadedDataToListenerWork(dataListeners);
  }

  public boolean doSendLoadedDataToListenerWork(DataListener<T> listener) {
    doAddListenerWork(listener);
    List<DataListener<T>> listeners = null;
    if (listener != null) {
      listeners = new ArrayList<>(1);
      listeners.add(listener);
    }
    return doSendLoadedDataToListenerWork(listeners);
  }

  private boolean doSendLoadedDataToListenerWork(final List<DataListener<T>> listeners) {
    if (!(state instanceof StateDone)) {
      setState(new StateDone(this));
    }
    if (listeners != null && !listeners.isEmpty()) {
      if (isMainThread()) {
        safeListenData(listeners, loadedData, throwable);
      } else {
        mainThreadHandler.post(() -> safeListenData(listeners, loadedData, throwable));
      }
    }
    return true;
  }

  private void safeListenData(List<DataListener<T>> listeners, T t, Throwable throwable) {
    for (DataListener<T> listener : listeners) {
      try {
        if (throwable != null) {
          listener.onError(throwable);
        } else {
          listener.onDataArrived(t);
        }
      } catch (Exception e) {
        Logger.error(e.getMessage());
        listener.onError(throwable);
      }
    }
  }

  private boolean isMainThread() {
    return Looper.getMainLooper() == Looper.myLooper();
  }

  private void setState(State state) {
    if (state != null) {
      if (this.state != null) {
        if (this.state.getClass() == state.getClass()) {
          return;
        }
      }
      this.state = state;
      Logger.debug("set state to:" + state.name());
    }
  }


  private void run() {
    loadedData = null;
    throwable = null;
    if (dataLoader == null || dataLoader.loader() == null) {
      throwable = new IllegalStateException("DataLoader is invalid");
      state.dataLoadFinished();
      return;
    }

    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
    }

    disposable = dataLoader.loader().subscribe(response -> {
      if (response == null) {
        throwable = new IllegalStateException("response is null");
      } else {
        loadedData = response;
      }
      state.dataLoadFinished();
    }, e -> {
      throwable = e;
      state.dataLoadFinished();
      Logger.error(e.getMessage());
    });
  }

}
