package com.sofar.widget.floating;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.annotation.NonNull;

/**
 * 针对手机屏幕拖拽
 */
public class DragHelper {

  private static final String TAG = "DragHelper";

  private PointF downPoint = new PointF();
  private PointF lastPoint = new PointF();

  private boolean isDragging;
  private int touchSlop;
  private final Callback callback;
  private final View dragView;

  public static DragHelper create(@NonNull View dragView, @NonNull Callback callback) {
    return new DragHelper(dragView.getContext(), dragView, callback);
  }

  private DragHelper(@NonNull Context context, @NonNull View dragView, @NonNull Callback callback) {
    touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    this.callback = callback;
    this.dragView = dragView;
  }

  public boolean shouldInterceptTouchEvent(MotionEvent ev) {
    Log.d(TAG, "InterceptTouchEvent:" + ev.getAction());
    int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        float x = ev.getRawX();
        float y = ev.getRawY();
        downPoint.set(x, y);
        lastPoint.set(x, y);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        float x = ev.getRawX();
        float y = ev.getRawY();
        float dx = x - downPoint.x;
        float dy = y - downPoint.y;
        if (!isDragging && (Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop)) {
          isDragging = true;
          callback.onDragStart();
        }
        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        isDragging = false;
        break;
      }
    }
    return isDragging;
  }

  public boolean processTouchEvent(MotionEvent ev) {
    Log.d(TAG, "TouchEvent:" + ev.getAction());
    int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        float x = ev.getRawX();
        float y = ev.getRawY();
        downPoint.set(x, y);
        lastPoint.set(x, y);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        float x = ev.getRawX();
        float y = ev.getRawY();
        int dx = (int) (x - lastPoint.x);
        int dy = (int) (y - lastPoint.y);
        if (!isDragging && (Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop)) {
          isDragging = true;
          callback.onDragStart();
        }
        if (isDragging) {
          dragTo(dx, dy);
          callback.onDragOffset(dx, dy);
          lastPoint.set(x, y);
        }
        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        if (isDragging) {
          isDragging = false;
          callback.onDragEnd();
        }
        break;
      }
    }
    return true;
  }

  private void dragTo(int dx, int dy) {
    int x = (int) (dragView.getX() + dx);
    int y = (int) (dragView.getY() + dy);
    Log.d(TAG, "x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy);
    if (dx != 0) {
      x = callback.clampX(x);
      dragView.setX(x);
    }
    if (dy != 0) {
      y = callback.clampY(y);
      dragView.setY(y);
    }
  }

  interface Callback {
    default void onDragStart() {}

    default void onDragEnd() {}

    default void onDragOffset(int dx, int dy) {}

    default int clampX(int x) {
      return x;
    }

    default int clampY(int y) {
      return y;
    }
  }

}
