package com.sofar.fun.play.viewbinder;

import android.view.View;
import android.view.ViewGroup;

import com.sofar.R;
import com.sofar.base.viewbinder.ViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.utility.DeviceUtil;

public class FeedVideoSizeViewBinder extends ViewBinder<Feed> {

  View videoRoot;

  @Override
  protected void onCreate() {
    super.onCreate();
    videoRoot = view.findViewById(R.id.video_root);
    ViewGroup.LayoutParams lp = videoRoot.getLayoutParams();
    lp.width = DeviceUtil.getMetricsWidth(context);
    lp.height = (int) (1.0f * lp.width * 9 / 16);
    videoRoot.setLayoutParams(lp);
  }


}
