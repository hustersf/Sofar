package com.sofar.widget.swipe;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

/**
 * 支持滑动返回的Activity需要设置透明背景主题
 * <item name="android:windowIsTranslucent">true</item>
 * <item name="android:windowBackground">@android:color/transparent</item>
 */
public class SwipeBack {

  public static SwipeLayout attach(Activity activity, SwipeLayout.OnSwipedListener func) {
    SwipeLayout layout = new SwipeLayout(activity);
    new Helper(layout).attachSwipeBack(activity, func);
    return layout;
  }

  public static SwipeLayout attach(Activity activity) {
    SwipeLayout layout = new SwipeLayout(activity);
    new Helper(layout).attachSwipeBack(activity, null);
    return layout;
  }

  static class Helper {

    final SwipeLayout mSwipeLayout;

    Helper(SwipeLayout swipeLayout) {
      mSwipeLayout = swipeLayout;
    }

    void attachSwipeBack(final Activity activity, final SwipeLayout.OnSwipedListener func) {
      if (activity == null || activity.getWindow() == null
        || !(activity.getWindow().getDecorView() instanceof ViewGroup)) {
        return;
      }

      ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
      if (decor != null) {
        if (decor.getChildCount() > 0) {
          View decorChild = decor.getChildAt(0);
          decor.removeView(decorChild);
          mSwipeLayout.addView(decorChild);
        }
        decor.addView(mSwipeLayout);
      }

      mSwipeLayout.setOnSwipedListener(new SwipeLayout.OnSwipedListener() {
        @Override
        public void onSwipeFinish() {
          activity.finish();
          activity.overridePendingTransition(0, 0);
          if (func != null) {
            func.onSwipeFinish();
          }
        }

        @Override
        public void onSwipeProgress(float progress) {
          int alpha = (int) (255 * 0.5 * (1 - progress));
          mSwipeLayout.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
          if (func != null) {
            func.onSwipeProgress(progress);
          }
        }
      });
    }

  }

}
