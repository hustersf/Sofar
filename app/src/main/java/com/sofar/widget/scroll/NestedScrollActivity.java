package com.sofar.widget.scroll;

import java.util.ArrayList;
import java.util.List;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.utility.DeviceUtil;
import com.sofar.widget.DataProvider;
import com.sofar.widget.nested.NestedWebView;
import com.sofar.widget.recycler.FeedAdapter;

/**
 * 测试嵌套滑动
 */
public class NestedScrollActivity extends AppCompatActivity {

  private static final String TAG = "NestedScrollActivity";

  ViewGroup root;
  NestedWebView webView;
  RecyclerView recyclerView;
  RecyclerView recyclerView2;

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

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.nest_scroll_activity);
    root = findViewById(R.id.root);
    initWebView();
    initRecyclerView();
    initRecyclerView2();
    initRecyclerView3();
  }

  private void initWebView() {
    webView = findViewById(R.id.web_view);
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webView.setWebViewClient(mWebViewClient);
    webView.loadUrl("https://leetcode-cn.com/problemset/all/");
  }

  private void initRecyclerView() {
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    FeedAdapter adapter = new FeedAdapter();
    recyclerView.setAdapter(adapter);
    setData(adapter);
    recyclerView.addOnChildAttachStateChangeListener(mOnChildAttachStateChangeListener);
    recyclerView.post(() -> {
      Log.d(TAG, "recyclerView h=" + recyclerView.getHeight());
      ViewGroup.LayoutParams lp = webView.getLayoutParams();
      lp.height = recyclerView.getHeight();
      webView.setLayoutParams(lp);
    });
  }

  private void initRecyclerView2() {
    recyclerView2 = findViewById(R.id.recycler_view2);
    recyclerView2.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    CommentAdapter adapter = new CommentAdapter();
    recyclerView2.setAdapter(adapter);
    setCommentData(adapter);
    recyclerView2.post(() -> {
      Log.d(TAG, "recyclerView2 h=" + recyclerView2.getHeight());
    });
  }

  private void initRecyclerView3() {
    RecyclerView recyclerView = findViewById(R.id.recycler_view3);
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