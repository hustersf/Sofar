package com.sofar.profiler.ui.floating

import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

class DragHelper private constructor(
  context: Context,
  private val dragView: View,
  private val callback: Callback
) {

  private val downPoint = PointF()
  private val lastPoint = PointF()

  private var isDragging = false
  private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

  companion object {
    private const val TAG = "DragHelper"

    @JvmStatic
    fun create(dragView: View, callback: Callback): DragHelper {
      return DragHelper(dragView.context, dragView, callback)
    }
  }

  fun shouldInterceptTouchEvent(ev: MotionEvent): Boolean {
    Log.d(TAG, "InterceptTouchEvent:${ev.action}")
    val x = ev.rawX
    val y = ev.rawY

    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        downPoint.set(x, y)
        lastPoint.set(x, y)
      }
      MotionEvent.ACTION_MOVE -> {
        val dx = x - downPoint.x
        val dy = y - downPoint.y
        if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
          isDragging = true
          callback.onDragStart()
        }
      }
      MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        isDragging = false
      }
    }
    return isDragging
  }

  fun processTouchEvent(ev: MotionEvent): Boolean {
    Log.d(TAG, "TouchEvent:${ev.action}")
    val x = ev.rawX
    val y = ev.rawY

    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        downPoint.set(x, y)
        lastPoint.set(x, y)
      }
      MotionEvent.ACTION_MOVE -> {
        val dx = (x - lastPoint.x).toInt()
        val dy = (y - lastPoint.y).toInt()

        if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
          isDragging = true
          callback.onDragStart()
        }

        if (isDragging) {
          dragTo(dx, dy)
          callback.onDragOffset(dx, dy)
          lastPoint.set(x, y)
        }
      }
      MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        if (isDragging) {
          isDragging = false
          callback.onDragEnd()
        }
      }
    }
    return true
  }

  private fun dragTo(dx: Int, dy: Int) {
    var x = (dragView.x + dx).toInt()
    var y = (dragView.y + dy).toInt()

    Log.d(TAG, "x=$x y=$y dx=$dx dy=$dy")

    if (dx != 0) {
      x = callback.clampX(x)
      dragView.x = x.toFloat()
    }
    if (dy != 0) {
      y = callback.clampY(y)
      dragView.y = y.toFloat()
    }
  }

  interface Callback {
    fun onDragStart() {}
    fun onDragEnd() {}
    fun onDragOffset(dx: Int, dy: Int) {}
    fun clampX(x: Int): Int = x
    fun clampY(y: Int): Int = y
  }
}