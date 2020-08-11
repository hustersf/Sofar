package com.sofar.fun.ad.strategy;

import androidx.annotation.NonNull;

import com.sofar.fun.ad.job.ParallelJob;
import com.sofar.fun.ad.task.CountTask;
import com.sofar.fun.ad.task.CountTaskFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @param <K> 提交一个K 类型的数据列表
 * @param <V> 返回一个V 类型的数据列表
 */
public class ParallelStrategy<K, V> {

  public Observable<List<V>> applyStrategy(@NonNull List<K> list, int count, int parallelCount,
                                           @NonNull CountTaskFactory<K> taskFactory) {
    return Observable.create((ObservableOnSubscribe<List<V>>) emitter -> {
      List<CountTask> tasks = new ArrayList<>();
      for (K info : list) {
        tasks.add(taskFactory.createTask(info));
      }

      ParallelJob job = new ParallelJob(tasks, count);
      job.setParallelCount(parallelCount);
      job.submit(results -> {
        emitter.onNext(results);
        emitter.onComplete();
      });
    }).observeOn(AndroidSchedulers.mainThread());

  }

}
