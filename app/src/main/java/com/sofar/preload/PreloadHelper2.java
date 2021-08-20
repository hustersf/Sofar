package com.sofar.preload;

import com.sofar.preloader.interfaces.DataListener;
import com.sofar.preloader.interfaces.DataLoader;
import com.sofar.preloader.interfaces.GroupedDataListener;
import com.sofar.preloader.interfaces.GroupedDataLoader;
import com.sofar.preloader.interfaces.PreloadListener;

import io.reactivex.Observable;

public class PreloadHelper2 {

  public static <T> DataLoader<T> loader(Observable<T> observable) {
    return new DataLoader<T>() {
      @Override
      public Observable<T> loader() {
        return observable;
      }
    };
  }

  public static <T> DataListener<T> listener(PreloadListener<T> listener) {
    return new DataListener<T>() {
      @Override
      public void onDataArrived(T t) {
        if (listener != null) {
          listener.onResponse(t);
        }
      }

      @Override
      public void onError(Throwable e) {
        if (listener != null) {
          listener.onError(e);
        }
      }
    };
  }

  public static <T> GroupedDataLoader<T> loader(String key, Observable<T> observable) {
    return new GroupedDataLoader<T>() {
      @Override
      public String keyInGroup() {
        return key;
      }

      @Override
      public Observable<T> loader() {
        return observable;
      }
    };
  }

  public static <T> GroupedDataListener<T> listener(String key, PreloadListener<T> listener) {
    return new GroupedDataListener<T>() {
      @Override
      public String keyInGroup() {
        return key;
      }

      @Override
      public void onDataArrived(T t) {
        if (listener != null) {
          listener.onResponse(t);
        }
      }

      @Override
      public void onError(Throwable e) {
        if (listener != null) {
          listener.onError(e);
        }
      }
    };
  }


}
