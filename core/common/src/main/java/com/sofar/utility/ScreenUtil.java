package com.sofar.utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

public class ScreenUtil {

  /**
   * 获取屏幕截图
   */
  public static Bitmap getScreenShot(Activity activity) {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap b1 = view.getDrawingCache();
    Rect frame = new Rect();
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
    int statusBarHeight = frame.top;
    DisplayMetrics dm = activity.getResources().getDisplayMetrics();
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
    view.destroyDrawingCache();
    return b;
  }

}


