package com.sofar.widget.scroll;

import java.util.ArrayList;
import java.util.List;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.utility.DeviceUtil;
import com.sofar.utility.ViewUtil;
import com.sofar.utility.statusbar.StatusBarUtil;
import com.sofar.widget.DataProvider;
import com.sofar.widget.floating.FloatingWidget;
import com.sofar.widget.nested.NestedArticleScrollLayout;
import com.sofar.widget.nested.NestedLinkRecyclerView;
import com.sofar.widget.nested.NestedWebView;
import com.sofar.widget.recycler.FeedAdapter;

/**
 * 测试嵌套滑动
 */
public class NestedScrollActivity extends AppCompatActivity {

  private static final String TAG = "NestedScrollActivity";

  private int height;
  NestedWebView webView;
  NestedLinkRecyclerView recyclerView;

  WebViewClient mWebViewClient = new WebViewClient() {
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
      handler.proceed();
      Log.e(TAG, "SslError=" + error.toString());
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
    }
  };

  RecyclerView.OnChildAttachStateChangeListener mOnChildAttachStateChangeListener =
    new RecyclerView.OnChildAttachStateChangeListener() {
      @Override
      public void onChildViewAttachedToWindow(@NonNull View view) {
        Log.d(TAG, "view position=" + recyclerView.getChildAdapterPosition(view));
      }

      @Override
      public void onChildViewDetachedFromWindow(@NonNull View view) {

      }
    };

  NestedArticleScrollLayout.OnScrollListener mOnScrollListener =
    new NestedArticleScrollLayout.OnScrollListener() {
      @Override
      public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        Log.i(TAG, "newState=" + newState);
      }


      @Override
      public void onNestedScrollStateChanged(@NonNull View target, int newState) {
        super.onNestedScrollStateChanged(target, newState);
        Log.i(TAG, "newState=" + newState + " " + target.toString());
      }
    };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.nest_scroll_activity2);
    height = DeviceUtil.getMetricsHeight(this) - StatusBarUtil.getStatusBarHeight(this);
    floatingWidget();
    initRefreshLayout();
    initWebView();
    initRecyclerView();
    initRecyclerView2();
    initRecyclerView3();
  }

  private void floatingWidget() {
    NestedArticleScrollLayout scrollLayout = findViewById(R.id.article_layout);
    if (scrollLayout == null) {
      return;
    }

    scrollLayout.addOnScrollListener(mOnScrollListener);
    FloatingWidget widget = new FloatingWidget(this);
    ViewUtil.inflate(widget, R.layout.nested_float_layout, true);
    widget.findViewById(R.id.scroll_to_web).setOnClickListener(v -> {
      if (webView != null) {
        scrollLayout.scrollToTarget(webView);
      }
    });
    widget.findViewById(R.id.scroll_to_recycler).setOnClickListener(v -> {
      if (recyclerView != null) {
        scrollLayout.scrollToTarget(recyclerView);
      }
    });
    widget.setScreenRatio(1.0f, 1.0f);
    new Handler().post(() -> {
      int actionBar = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
      widget.setInsets(0, 0, 0, actionBar);
      widget.attach();
    });
  }

  private void initRefreshLayout() {
    SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
    if (refreshLayout == null) {
      return;
    }

    refreshLayout.setOnRefreshListener(() -> refreshLayout.postDelayed(() -> {
      refreshLayout.setRefreshing(false);
    }, 2000));
  }

  private void initWebView() {
    webView = findViewById(R.id.web_view);
    ViewGroup.LayoutParams lp = webView.getLayoutParams();
    lp.height = height;
    webView.setLayoutParams(lp);
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webView.setWebViewClient(mWebViewClient);
    webView.loadUrl("https://leetcode-cn.com/problemset/all/");
  }

  private void initRecyclerView() {
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setMaxHeight(height);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    FeedAdapter adapter = new FeedAdapter();
    recyclerView.setAdapter(adapter);
    setData(adapter);
    recyclerView.addOnChildAttachStateChangeListener(mOnChildAttachStateChangeListener);
    recyclerView.post(() -> {
      Log.d(TAG, "recyclerView h=" + recyclerView.getHeight());
    });
  }

  private void initRecyclerView2() {
    RecyclerView recyclerView = findViewById(R.id.recycler_view2);
    if (recyclerView == null) {
      return;
    }
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    CommentAdapter adapter = new CommentAdapter();
    recyclerView.setAdapter(adapter);
    setCommentData(adapter);
    recyclerView.post(() -> {
      Log.d(TAG, "recyclerView2 h=" + recyclerView.getHeight());
    });
  }

  private void initRecyclerView3() {
    RecyclerView recyclerView = findViewById(R.id.recycler_view3);
    if (recyclerView == null) {
      return;
    }
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    FeedAdapter adapter = new FeedAdapter();
    recyclerView.setAdapter(adapter);
    setData(adapter);
    recyclerView.post(() -> {
      Log.d(TAG, "recyclerView3 h=" + recyclerView.getHeight());
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

  private void setCommentData(CommentAdapter adapter) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      list.add("评论" + i);
    }
    adapter.setList(list);
    adapter.notifyDataSetChanged();
  }

  private int getActionBarHeight() {
    TypedArray actionbarSizeTypedArray = obtainStyledAttributes(new int[]{
      android.R.attr.actionBarSize
    });

    float actionBarHeight = actionbarSizeTypedArray.getDimension(0, 0);
    return (int) actionBarHeight;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    recyclerView.removeOnChildAttachStateChangeListener(mOnChildAttachStateChangeListener);
  }

  public static class CommentAdapter extends RecyclerAdapter<String> {

    @NonNull
    @Override
    protected View onCreateView(ViewGroup parent, int viewType) {
      TextView textView = new TextView(parent.getContext());
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = DeviceUtil.dp2px(parent.getContext(), 100);
      ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, height);
      textView.setLayoutParams(lp);
      textView.setGravity(Gravity.CENTER);
      return textView;
    }

    @NonNull
    @Override
    protected RecyclerViewBinder onCreateViewBinder(int viewType) {
      RecyclerViewBinder viewBinder = new RecyclerViewBinder();
      viewBinder.addViewBinder(new CommentViewBinder());
      return viewBinder;
    }
  }

  public static class CommentViewBinder extends RecyclerViewBinder<String> {

    @Override
    protected void onBind(String data) {
      super.onBind(data);
      if (view instanceof TextView) {
        TextView textView = (TextView) view;
        textView.setText(data);
        if (viewAdapterPosition % 2 == 0) {
          textView.setBackgroundColor(Color.WHITE);
        } else {
          textView.setBackgroundColor(Color.LTGRAY);
        }
      }
    }
  }
}
