package com.sofar.fun.play;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

public class AutoPlayListActivity extends AppCompatActivity {

  PublishSubject<AutoPlaySignal> playSignal = PublishSubject.create();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("自动播放列表");
    setContentView(R.layout.fun_auto_play_activity);

    initList();
  }

  private void initList() {
    RecyclerView playRecycler = findViewById(R.id.play_recycler);
    playRecycler.setLayoutManager(new LinearLayoutManager(this));
    AutoPlayAdapter adapter = new AutoPlayAdapter(playSignal);
    List<Feed> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Feed feed = new Feed();
      feed.title = "视频" + i;
      list.add(feed);
    }
    adapter.setList(list);
    playRecycler.setAdapter(adapter);

    AutoPlayHelper helper = new AutoPlayHelper();
    helper.attachToRecyclerView(playRecycler);
    helper.setOnSelectListener((childView, position) -> {
      AutoPlaySignal signal = new AutoPlaySignal();
      signal.command = AutoPlaySignal.Command.PLAY_POSITION;
      signal.playPosition = position;
      playSignal.onNext(signal);
    });

    playSignal.subscribe(signal -> {
      if (TextUtils.equals(signal.command, AutoPlaySignal.Command.PLAY_STATUS)) {
        if (TextUtils.equals(signal.playStatus, AutoPlaySignal.PlayStatus.PLAY_FINISH)) {
          helper.playFinish();
        }
      }
    });
  }
}
