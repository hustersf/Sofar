package com.sofar.fun.play;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;

import io.reactivex.subjects.PublishSubject;

public class AutoPlayViewBinder extends RecyclerViewBinder<Feed> implements Playable {

  TextView titleTv;
  TextView showTimeTv;

  Handler handler = new Handler(Looper.getMainLooper());
  final int IMAGE_AD_STAY_TIME = 8_000;

  private long duration;
  private Runnable timeRunnable = new Runnable() {
    @Override
    public void run() {
      if (duration <= 0) {
        playFinish();
        stopTimer();
        return;
      }

      int seconds = (int) (duration / DateUtils.SECOND_IN_MILLIS);
      String prefix = "";
      if (seconds < 10) {
        prefix = "0";
      }
      showTimeTv.setText(prefix + seconds + " | 广告");
      duration -= 1000;
      handler.postDelayed(timeRunnable, 1000);
    }
  };

  @NonNull
  PublishSubject<AutoPlaySignal> playSignal;

  public AutoPlayViewBinder(@NonNull PublishSubject<AutoPlaySignal> playSignal) {
    this.playSignal = playSignal;
  }

  @Override
  protected void onCreate() {
    super.onCreate();
    titleTv = view.findViewById(R.id.title);
    showTimeTv = view.findViewById(R.id.show_time);
  }

  @Override
  protected void onBind(Feed data) {
    super.onBind(data);
    titleTv.setText(data.title);

    playSignal.subscribe(signal -> {
      if (TextUtils.equals(signal.command, AutoPlaySignal.Command.PLAY_POSITION)) {
        if (signal.playPosition == viewAdapterPosition) {
          start();
        } else {
          stop();
        }
      }
    });
  }

  private void startTimer() {
    if (duration <= 0) {
      duration = IMAGE_AD_STAY_TIME;
    }
    showTimeTv.setVisibility(View.VISIBLE);
    handler.removeCallbacks(timeRunnable);
    handler.post(timeRunnable);
  }

  private void stopTimer() {
    showTimeTv.setVisibility(View.GONE);
    handler.removeCallbacks(timeRunnable);
  }

  private void playFinish() {
    AutoPlaySignal signal = new AutoPlaySignal();
    signal.playStatus = AutoPlaySignal.PlayStatus.PLAY_FINISH;
    signal.command = AutoPlaySignal.Command.PLAY_STATUS;
    playSignal.onNext(signal);
  }

  @Override
  public void start() {
    startTimer();
  }

  @Override
  public void stop() {
    stopTimer();
  }
}