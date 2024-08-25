package com.sofar.widget.scroll;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;


/**
 * 测试联动滚动
 */
public class LinkedScrollActivity extends AppCompatActivity {

  private static final String TAG = "LinkedScrollActivity";

  private HorizontalScrollView scrollView;

  private View gestureView;
  private GestureDetector detector;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.linked_scroll_activity);
    scrollView=findViewById(R.id.scroll_view);
    gestureView = findViewById(R.id.gesture_view);

    gestureView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
      }
    });

    detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll x=" + distanceX);
        scrollView.scrollBy((int) distanceX,0);
        return super.onScroll(e1, e2, distanceX, distanceY);
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling velocityX=" + velocityX);
        scrollView.fling(-(int) velocityX);
        return super.onFling(e1, e2, velocityX, velocityY);
      }
    });
  }


}
