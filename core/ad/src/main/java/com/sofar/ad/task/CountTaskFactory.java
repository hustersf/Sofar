package com.sofar.ad.task;

import androidx.annotation.NonNull;


public interface CountTaskFactory<K> {

  @NonNull
  CountTask createTask(K info);

}
