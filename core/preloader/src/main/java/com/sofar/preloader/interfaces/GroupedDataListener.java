package com.sofar.preloader.interfaces;

public interface GroupedDataListener<DATA> extends DataListener<DATA> {
  String keyInGroup();
}
