package com.sofar.download;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;

public class DownloadActivity extends AppCompatActivity {

  private final static String TAG = "DownloadActivity";

  private String downloadUrl = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4";

  ProgressBar progressBar;
  TextView fileNameTv;
  Button startBtn;
  Button cancelBtn;

  int downloadId;
  DownloadRequest request;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.download_activity);
    setTitle("下载库测试页");

    progressBar = findViewById(R.id.progress);
    fileNameTv = findViewById(R.id.file_name);
    startBtn = findViewById(R.id.download_start);
    cancelBtn = findViewById(R.id.download_cancel);

    createDownloadRequest();
    startBtn.setOnClickListener(v -> {
      downloadId = DownloadManager.get().add(request);
    });
    cancelBtn.setOnClickListener(v -> {
      DownloadManager.get().cancel(downloadId);
    });
  }


  private void createDownloadRequest() {
    request = new DownloadRequest(Uri.parse(downloadUrl));
    request.setDownloadListener(new DownloadListener() {
      @Override
      public void onDownloadConnected(@NonNull DownloadRequest request) {
        Log.d(TAG, "onDownloadConnected");
        fileNameTv.setText(request.fileName + " 建立连接");
      }

      @Override
      public void onDownloadWait(@NonNull DownloadRequest request) {
        Log.d(TAG, "onDownloadWait");
        fileNameTv.setText(request.fileName + " 等待下载");
      }

      @Override
      public void onDownloadCanceled(@NonNull DownloadRequest request) {
        Log.d(TAG, "onDownloadCanceled");
        fileNameTv.setText(request.fileName + " 取消下载");
      }

      @Override
      public void onDownloadComplete(@NonNull DownloadRequest request) {
        Log.d(TAG, "onDownloadComplete");
        fileNameTv.setText(request.fileName + " 下载完成");
      }

      @Override
      public void onDownloadFailed(@NonNull DownloadRequest request, int code, String message) {
        Log.d(TAG, "onDownloadFailed code=" + code + ":message=" + message);
        fileNameTv.setText(request.fileName + " 下载失败");
      }

      @Override
      public void onDownloadProgress(@NonNull DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
        fileNameTv.setText(request.fileName + " 下载中..." + progress + "%");
        progressBar.setProgress(progress);
      }
    });
  }
}
