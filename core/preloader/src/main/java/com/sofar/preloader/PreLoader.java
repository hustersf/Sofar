package com.sofar.preloader;

import java.util.List;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.interfaces.DataLoader;
import com.sofar.preloader.interfaces.GroupedDataListener;
import com.sofar.preloader.interfaces.GroupedDataLoader;

/**
 * Entrance for pre-load data
 */
public class PreLoader {

  public static <T> PreLoaderWrapper<T> just(DataLoader<T> loader) {
    return just(loader, (DataListener<T>) null);
  }

  public static <T> PreLoaderWrapper<T> just(DataLoader<T> loader, DataListener<T> listener) {
    PreLoaderWrapper<T> preLoader = new PreLoaderWrapper<>(loader, listener);
    preLoader.preLoad();
    return preLoader;
  }

  public static <T> PreLoaderWrapper<T> just(DataLoader<T> loader,
    List<DataListener<T>> listeners) {
    PreLoaderWrapper<T> preLoader = new PreLoaderWrapper<>(loader, listeners);
    preLoader.preLoad();
    return preLoader;
  }

  /**
   * 预加载一个任务,返回一个id
   * 不同页面，不同场景，可通过id 复用任务返回结果
   */
  public static <T> long preLoad(DataLoader<T> loader) {
    return PreLoaderPool.getDefault().preLoad(loader);
  }

  public static <T> long preLoad(DataLoader<T> loader, DataListener<T> listener) {
    return PreLoaderPool.getDefault().preLoad(loader, listener);
  }

  public static <T> long preLoad(DataLoader<T> loader, List<DataListener<T>> listeners) {
    return PreLoaderPool.getDefault().preLoad(loader, listeners);
  }

  /**
   * 预加载一组任务,返回一个id
   * 组内的每一个子任务有一个唯一的 {@link GroupedDataLoader#keyInGroup()}
   */
  public static long preLoad(GroupedDataLoader... loaders) {
    boolean allNull = true;
    for (DataLoader loader : loaders) {
      if (loader != null) {
        allNull = false;
        break;
      }
    }
    if (allNull) {
      return -1;
    }
    return PreLoaderPool.getDefault().preLoadGroup(loaders);
  }

  public static boolean listenData(int id) {
    return PreLoaderPool.getDefault().listenData(id);
  }

  public static <T> boolean listenData(int id, DataListener<T> dataListener) {
    return PreLoaderPool.getDefault().listenData(id, dataListener);
  }

  /**
   * 通过id，可复用任务
   * 通过 {@link GroupedDataListener#keyInGroup()}，关联子任务
   */
  public static boolean listenData(int id, GroupedDataListener... listeners) {
    return PreLoaderPool.getDefault().listenData(id, listeners);
  }

  public static <T> boolean removeListener(int id, DataListener<T> dataListener) {
    return PreLoaderPool.getDefault().removeListener(id, dataListener);
  }

  public static boolean exists(int id) {
    return PreLoaderPool.getDefault().exists(id);
  }

  public static boolean refresh(int id) {
    return PreLoaderPool.getDefault().refresh(id);
  }

  public static boolean refresh(int id, String key) {
    return PreLoaderPool.getDefault().refresh(id, key);
  }

  public static boolean destroy(int id) {
    return PreLoaderPool.getDefault().destroy(id);
  }

  public static boolean destroyAll() {
    return PreLoaderPool.getDefault().destroyAll();
  }

  public static PreLoaderPool newPool() {
    return new PreLoaderPool();
  }
}
