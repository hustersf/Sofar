package com.sofar.ad.strategy;

import androidx.annotation.NonNull;

import com.sofar.ad.job.SerialJob;
import com.sofar.ad.log.AdDebug;
import com.sofar.ad.task.CountTask;
import com.sofar.ad.task.CountTaskFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @param <K> 提交一个K 类型的数据列表
 * @param <V> 返回一个V 类型的数据列表
 */
public class SerialStrategy<K, V> {

  public Observable<List<V>> applyStrategy(@NonNull List<K> list, int count,
    @NonNull CountTaskFactory<K> taskFactory) {
    AdDebug.d("执行串行策略:广告位数量=%d,需要%d条广告", list.size(), count);
    return Observable.create((ObservableOnSubscribe<List<V>>) emitter -> {
      List<CountTask> tasks = new ArrayList<>();
      for (K info : list) {
        tasks.add(taskFactory.createTask(info));
      }

      SerialJob job = new SerialJob(tasks, count);
      job.submit(results -> {
        emitter.onNext(results);
        emitter.onComplete();
      });
    }).observeOn(AndroidSchedulers.mainThread());
  }

}
