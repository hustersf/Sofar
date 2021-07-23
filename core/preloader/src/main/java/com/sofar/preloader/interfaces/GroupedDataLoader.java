package com.sofar.preloader.interfaces;

public interface GroupedDataLoader<DATA> extends DataLoader<DATA> {
  String keyInGroup();
}
