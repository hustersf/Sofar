package com.sofar.widget.scroll;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.sofar.R;
import com.sofar.fun.play.Feed;
import com.sofar.widget.DataProvider;
import com.sofar.widget.recycler.FeedAdapter;


/**
 * 测试联动滚动示例
 * CoordinatorLayout + AppBarLayout + RecyclerView
 * 类似B站视频详情页效果：列表向上滚动时，视频区域逐渐缩小，但视频比例始终保持不变
 *
 * 使用规则
 * 1.AppBarLayout 必须是 CoordinatorLayout 到直接子布局
 * 2.必须设置 app:layout_behavior，且位置必须放到与 AppBarLayout 同一层级View上
 * 3.enterAlwaysCollapsed需要配合enterAlways使用
 * 4.AppBarLayout 的第一个子View未设置scrollFlags,后面的子View设置也无效
 * <p>
 * layout_scrollFlags 用法
 * 0.noScroll: View保持静止，不随RecyclerView滚动
 * 1.scroll: View保持和RecyclerView同步滚动
 * 2.scroll_enterAlways: 反向滑动，View先出现，然后recycler继续滑动
 * 3.scroll_enterAlways_enterAlwaysCollapsed(minHeight):
 * 反向滑动，View先出现（只出现minHeight高度），然后recycler继续滑动到顶部，才出现View全部高度。
 * 4.scroll_exitUntilCollapsed(minHeight):正向滑动，View收起至minHeight高度便不再继续滑动，保持置顶悬挂
 * 5.scroll_snap:随RecyclerView滑动，区别是自带吸附功能,未滑动到指定位置，将自动吸附
 */
public class AppbarLayoutActivity extends AppCompatActivity {

  private static final String TAG = "AppbarLayoutActivity";

  private RecyclerView recyclerView;
  private AppBarLayout appBarLayout;
  private ImageView imageView;
  private ViewGroup videoLayout;

  private AppBarLayout.OnOffsetChangedListener offsetChangedListener =
    new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        imageView.setPadding(imageView.getPaddingLeft(), -verticalOffset,
          imageView.getPaddingRight(), imageView.getPaddingBottom());
      }
    };


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_bar_layout_activity);
    initAppBarLayout();
    initRecyclerView();
  }

  private void initAppBarLayout() {
    appBarLayout = findViewById(R.id.app_bar_layout);
    imageView = findViewById(R.id.image);
    videoLayout = findViewById(R.id.video_layout);

    appBarLayout.addOnOffsetChangedListener(offsetChangedListener);
  }

  private void initRecyclerView() {
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    FeedAdapter adapter = new FeedAdapter();
    recyclerView.setAdapter(adapter);
    setData(adapter);
    recyclerView.post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "h=" + recyclerView.getHeight());
      }
    });
  }

  private void setData(FeedAdapter adapter) {
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    appBarLayout.removeOnOffsetChangedListener(offsetChangedListener);
  }
}
