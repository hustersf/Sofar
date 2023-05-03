package com.sofar.widget.recycler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sofar.R;
import com.sofar.utility.FileUtil;
import com.sofar.widget.recycler.model.College;
import com.sofar.widget.recycler.model.CollegeWrapper;
import com.sofar.widget.recycler.model.CollegeZone;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GroupRecyclerActivity extends AppCompatActivity {

  SwipeRefreshLayout refreshLayout;
  RecyclerView recyclerView;
  CollegeAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.recycler_activity);
    setTitle("分组列表测试");

    refreshLayout = findViewById(R.id.refresh_layout);
    refreshLayout.setEnabled(false);

    initRecycler();

    Observable.fromCallable(() -> {
        String jsonStr = FileUtil.getTextFromAssets(this, "json/college.json");
        CollegeWrapper collegeWrapper = new Gson().fromJson(jsonStr, CollegeWrapper.class);
        Map<String, CollegeZone> zoneMap = new HashMap<>();
        for (CollegeZone zone : collegeWrapper.zone) {
          zoneMap.put(zone.id, zone);
        }
        for (College college : collegeWrapper.university) {
          CollegeZone collegeZone = zoneMap.get(college.zone);
          if (collegeZone != null) {
            collegeZone.colleges.add(college);
          }
        }
        return collegeWrapper.zone;
      }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(collegeZones -> updateList(collegeZones));
  }

  private void initRecycler() {
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    adapter = new CollegeAdapter();
    recyclerView.setAdapter(adapter);
  }

  private void updateList(List<CollegeZone> list) {
    adapter.setGroups(list);
    adapter.notifyDataSetChanged();
  }

}
