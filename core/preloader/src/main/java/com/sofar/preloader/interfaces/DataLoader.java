package com.sofar.preloader.interfaces;

import io.reactivex.Observable;

public interface DataLoader<DATA> {
  Observable<DATA> loader();
}
