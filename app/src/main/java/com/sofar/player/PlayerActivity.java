package com.sofar.player;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.audio.record.AudioRecord;
import com.sofar.base.permission.PermissionUtil;
import com.sofar.player.core.AudioPlayer;
import com.sofar.player.core.VideoPlayer;
import com.sofar.utility.FileUtil;
import com.sofar.utility.ToastUtil;
import com.sofar.utility.ViewUtil;

import java.io.File;

public class PlayerActivity extends AppCompatActivity {

  String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

  VideoPlayer player;
  TextureView playerTexture;

  Button audioRecordStart;
  Button audioRecordStop;
  Button audioRecordPlay;

  ImageView shotImage;
  Handler mHandler = new Handler();
  int LOOP_INTERVAL = 1000;
  Runnable shotRunnable = new Runnable() {
    @Override
    public void run() {
      Bitmap bitmap = ViewUtil.getTextureViewShot(playerTexture);
      shotImage.setImageBitmap(bitmap);
      mHandler.postDelayed(this, LOOP_INTERVAL);
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setTitle("播放器测试页面");

    shotImage = findViewById(R.id.shot_image);

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

    mHandler.postDelayed(shotRunnable, 1000);
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
    mHandler.removeCallbacks(shotRunnable);
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
    AudioPlayer audioPlayer = new AudioPlayer(this);
    audioRecordStart.setOnClickListener(v -> {
      audioRecord.start();
    });

    audioRecordStop.setOnClickListener(v -> {
      if (audioRecord.stop()) {
        ToastUtil.startShort(this, "录音文件保存至" + filePath);
        audioPlayer.setUri(filePath);
      } else {
        ToastUtil.startShort(this, "请先录音");
      }
    });

    audioRecordPlay.setOnClickListener(v -> {
      audioPlayer.start();
    });
  }
}
