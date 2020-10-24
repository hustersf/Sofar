package com.sofar.player;

import android.os.Bundle;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.player.core.VideoPlayer;

public class PlayerActivity extends AppCompatActivity {

  String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

  VideoPlayer player;
  TextureView playerTexture;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setTitle("播放器测试页面");

    playerTexture = findViewById(R.id.player_texture);
    player = new VideoPlayer(videoUrl, playerTexture);
    player.start();
  }

  @Override
  protected void onResume() {
    super.onResume();
    player.resume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    player.pause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    player.stop();
  }
}
