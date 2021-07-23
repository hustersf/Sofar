package com.sofar.preloader.interfaces;

public interface DataListener<DATA> {

  void onDataArrived(DATA data);

  void onError(Throwable e);
}
