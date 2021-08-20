package com.sofar.preload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.preloader.PreLoader;
import com.sofar.preloader.annotation.PreLoad;
import com.sofar.preloader.interfaces.PreloadListener;

@PreLoad
public class PreLoadActivity extends AppCompatActivity {

  private static String KEY_PRELOAD_ID = "preloadId";
  private static long launchStart = 0;
  private static long activityCreate = 0;

  TextView dataTv;
  TextView data1Tv;
  TextView data2Tv;

  long preloadId;

  PreloadListener<String> githubListener = new PreloadListener<String>() {
    @Override
    public void onResponse(String response) {
      data1Tv.setText(costTime("github数据请求", launchStart) + "  "
        + costTime("用户等待数据", activityCreate));
    }
  };

  PreloadListener<String> bannerListener = new PreloadListener<String>() {
    @Override
    public void onResponse(String response) {
      data2Tv.setText(costTime("banner数据请求", launchStart) + "  "
        + costTime("用户等待数据", activityCreate));
    }
  };


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("数据预加载测试页面");
    setContentView(R.layout.preload_activity);
    dataTv = findViewById(R.id.data_result);
    data1Tv = findViewById(R.id.data1_result);
    data2Tv = findViewById(R.id.data2_result);

    dataTv.setText(costTime("启动Activity", launchStart));
    activityCreate = System.currentTimeMillis();
    preloadId = getIntent().getLongExtra(KEY_PRELOAD_ID, -1);
    PreLoader.listenData(preloadId, PreloadHelper.githubListener(githubListener),
      PreloadHelper.bannerListener(bannerListener));
  }

  public static void launch(@NonNull Context context) {
    launchStart = System.currentTimeMillis();
    Intent intent = new Intent(context, PreLoadActivity.class);
    long preloadId = PreLoader.preLoad(PreloadHelper.gitHubLoader(), PreloadHelper.bannerLoader());
    intent.putExtra(KEY_PRELOAD_ID, preloadId);
    context.startActivity(intent);
  }

  private String costTime(String event, long start) {
    return event + "事件耗时=" + (System.currentTimeMillis() - start) + "ms";
  }

}
