package com.sofar.player;

import android.Manifest;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.audio.record.AudioRecord;
import com.sofar.base.permission.PermissionUtil;
import com.sofar.player.core.AudioPlayer;
import com.sofar.player.core.VideoPlayer;
import com.sofar.utility.FileUtil;
import com.sofar.utility.ToastUtil;

import java.io.File;

public class PlayerActivity extends AppCompatActivity {

  String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

  VideoPlayer player;
  TextureView playerTexture;

  Button audioRecordStart;
  Button audioRecordStop;
  Button audioRecordPlay;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setTitle("播放器测试页面");

    playerTexture = findViewById(R.id.player_texture);
    player = new VideoPlayer(videoUrl, playerTexture);
    player.start();

    String permissionStr = Manifest.permission.RECORD_AUDIO;
    PermissionUtil.requestPermission(this, permissionStr).subscribe(permission -> {
      if (permission.granted) {
        audio();
      } else {
        ToastUtil.startShort(this, "需要录音权限");
      }
    });
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

  private void audio() {
    audioRecordStart = findViewById(R.id.audio_record_start);
    audioRecordStop = findViewById(R.id.audio_record_stop);
    audioRecordPlay = findViewById(R.id.audio_record_play);

    File dirFile = new File(FileUtil.getCacheDir(this).getPath() + "/audio");
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }
    String filePath = dirFile.getAbsolutePath() + "/record.3gp";
    AudioRecord audioRecord = new AudioRecord(filePath);
    AudioPlayer audioPlayer = new AudioPlayer(this, filePath);
    audioRecordStart.setOnClickListener(v -> {
      audioRecord.start();
    });

    audioRecordStop.setOnClickListener(v -> {
      if (audioRecord.stop()) {
        ToastUtil.startShort(this, "录音文件保存至" + filePath);
        audioPlayer.resetUri(filePath);
      }
    });

    audioRecordPlay.setOnClickListener(v -> {
      audioPlayer.start();
    });
  }
}
