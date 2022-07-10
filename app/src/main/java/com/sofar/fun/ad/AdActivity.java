package com.sofar.fun.ad;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.ad.AdFacade;
import com.sofar.ad.feed.model.FeedAd;
import com.sofar.ad.log.AdDebug;
import com.sofar.ad.model.AdInfo;
import com.sofar.ad.model.AdPondConfig;
import com.sofar.ad.util.AdUtil;

public class AdActivity extends AppCompatActivity {

  EditText mAdRequestCountView;
  EditText mAdParallelCountView;
  EditText mAdPreloadCountView;

  Button mAdStartButton;
  TextView mAdResultView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ad_activity);
    mAdRequestCountView = findViewById(R.id.ad_request_count);
    mAdParallelCountView = findViewById(R.id.ad_parallel_count);
    mAdPreloadCountView = findViewById(R.id.ad_preload_count);

    mAdStartButton = findViewById(R.id.ad_start);
    mAdResultView = findViewById(R.id.ad_result);

    AdDebug.setEnableDebug(true);
    mAdStartButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startRequestAd();
      }
    });
  }

  private void startRequestAd() {
    AdPondConfig.AdPondInfo adPondInfo = new AdPondConfig.AdPondInfo();
    adPondInfo.count = getCount(mAdRequestCountView);
    adPondInfo.parallelCount = getCount(mAdParallelCountView);
    adPondInfo.preloadCount = getCount(mAdPreloadCountView);
    adPondInfo.adInfos = getAdInfoList();

    mAdResultView.setText("请求广告中...");
    AdFacade.get().getFeedAds(adPondInfo, adPondInfo.count).subscribe(
      feedAds -> {
        showAdResult(feedAds);
      }, throwable -> {
        showFailedResult(throwable);
      });
  }


  private void showAdResult(List<FeedAd> feedAds) {
    StringBuffer sb = new StringBuffer();
    sb.append("广告请求结果:" + System.currentTimeMillis() + "\n");
    sb.append("广告返回条数:");
    sb.append(feedAds.size());
    sb.append("\n");
    for (int i = 0; i < feedAds.size(); i++) {
      FeedAd ad = feedAds.get(i);
      sb.append("第" + i + "条广告:");
      sb.append(AdUtil.printAdInfo(ad));
      sb.append("来源:" + (ad.fromCache ? "cache" : "remote"));
      sb.append("\n");
    }
    mAdResultView.setText(sb.toString());
  }


  private void showFailedResult(Throwable throwable) {
    StringBuffer sb = new StringBuffer();
    sb.append("广告请求结果:\n");
    sb.append("失败信息:\n");
    mAdResultView.setText(throwable.toString());
  }

  private int getCount(@NonNull EditText editText) {
    String str = editText.getText().toString().trim();
    try {
      return Integer.parseInt(str);
    } catch (Exception e) {
    }
    return 0;
  }

  private List<AdInfo> getAdInfoList() {
    List<AdInfo> list = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      AdInfo adInfo = new AdInfo();
      adInfo.adProvider = AdInfo.TT;
      adInfo.adCodeId = "100" + i;
      list.add(adInfo);
    }
    return list;
  }

}
