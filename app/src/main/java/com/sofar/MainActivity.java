package com.sofar;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.sofar.base.location.LocationProvider;
import com.sofar.main.MainItemDecoration;
import com.sofar.main.MainListAdapter;
import com.sofar.profiler.MonitorCallback;
import com.sofar.profiler.MonitorManager;

public class MainActivity extends AppCompatActivity {

  RecyclerView recyclerView;
  private static final String TAG = "MainActivity";

  MonitorCallback callback = new MonitorCallback() {
    @Override
    public void onFrameRate(int frameRate) {
      Log.d(TAG, "FPS=" + frameRate);
    }

    @Override
    public void onCpuRate(float cpuRate) {
      Log.d(TAG, "cpu使用率=" + cpuRate);
    }

    @Override
    public void onThreadCount(int count) {
      Log.d(TAG, "线程数=" + count);
    }

    @Override
    public void onMemoryInfo(@NotNull String info) {
      Log.d(TAG, "内存信息=" + info);
    }

    @Override
    public void onFDCount(int count) {
      Log.d(TAG, "FD数=" + count);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    recyclerView = findViewById(R.id.main_recycler);
    MainListAdapter adapter = new MainListAdapter();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(new MainItemDecoration(this));

    LocationProvider.getInstance().startLocation();

    MonitorManager.register(callback);
    MonitorManager.start();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    MonitorManager.unregister(callback);
    MonitorManager.stop();
  }
}
