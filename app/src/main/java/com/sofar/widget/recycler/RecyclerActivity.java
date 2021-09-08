package com.sofar.widget.recycler;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sofar.R;
import com.sofar.fun.play.Feed;
import com.sofar.utility.ToastUtil;
import com.sofar.widget.DataProvider;

public class RecyclerActivity extends AppCompatActivity {

  RecyclerView recyclerView;
  FeedAdapter adapter;

  SwipeRefreshLayout refreshLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.recycler_activity);
    setTitle("RecyclerView复用机制测试");

    initRefreshLayout();
    initRecycler();
  }

  private void initRefreshLayout() {
    refreshLayout = findViewById(R.id.refresh_layout);
    refreshLayout.setOnRefreshListener(() -> {
      ToastUtil.startShort(this, "刷新成功");
      new Handler().postDelayed(() -> setData(), 200);
    });
  }

  private void initRecycler() {
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    adapter = new FeedAdapter();
    recyclerView.setAdapter(adapter);
    setData();
  }

  private void setData() {
    refreshLayout.setRefreshing(false);
    List<String> imgUrls = DataProvider.imgUrls();
    List<Feed> list = new ArrayList<>();
    for (int i = 0; i < imgUrls.size(); i++) {
      Feed feed = new Feed();
      feed.title = "视频" + i;
      feed.imgUrl = imgUrls.get(i);
      list.add(feed);
    }
    adapter.setList(list);
    adapter.notifyDataSetChanged();
  }

  private void setData2() {
    refreshLayout.setRefreshing(false);
    List<Integer> drawableIds = DataProvider.drawableIds();
    List<Feed> list = new ArrayList<>();
    for (int i = 0; i < drawableIds.size(); i++) {
      Feed feed = new Feed();
      feed.title = "视频" + i;
      feed.drawableId = drawableIds.get(i);
      list.add(feed);
    }
    adapter.setList(list);
    adapter.notifyDataSetChanged();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    recyclerView.setAdapter(null);
  }
}
