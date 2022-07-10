package com.sofar.ad.task;

import androidx.annotation.NonNull;

import java.util.List;

public interface ResultCallback<T> {

  void onResult(@NonNull List<T> results);

}
