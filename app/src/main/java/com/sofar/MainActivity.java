package com.sofar;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sofar.base.location.LocationProvider;
import com.sofar.config.ABTest;
import com.sofar.config.ConfigManager;
import com.sofar.config.SystemConfig;
import com.sofar.fun.play.Feed;
import com.sofar.main.MainItemDecoration;
import com.sofar.main.MainListAdapter;
import com.sofar.utility.FileUtil;
import com.sofar.widget.recycler.overscroll.VerticalEdgeEffectFactory;

public class MainActivity extends AppCompatActivity {

  RecyclerView recyclerView;
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    recyclerView = findViewById(R.id.main_recycler);
    MainListAdapter adapter = new MainListAdapter();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(new MainItemDecoration(this));
    recyclerView.setEdgeEffectFactory(new VerticalEdgeEffectFactory());

    LocationProvider.getInstance().startLocation();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    writeConfig();
  }

  private void writeConfig() {
    String jsonStr = FileUtil.getTextFromAssets(this, "json/config_test.json");
    ConfigManager.get().update(new Gson().fromJson(jsonStr, JsonObject.class));

    Feed feed = ConfigManager.get().getValue("feed", Feed.class, new Feed());
    Log.d("config222", "feed title=" + feed.title);

    int enterLastTab = ABTest.getEnterLastTab();
    Log.d("config222", "enterLastTab=" + enterLastTab);

    Feed feed2 = SystemConfig.getFeed();
    if (feed2 != null) {
      Log.d("config222", "feed2=" + feed2.title);
    }

    Log.d("config222", "title=" + SystemConfig.getTitle());
  }
}
