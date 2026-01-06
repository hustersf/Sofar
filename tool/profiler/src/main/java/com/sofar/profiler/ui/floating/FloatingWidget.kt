package com.sofar.profiler.ui.floating

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.sofar.profiler.getMetricsHeight
import com.sofar.profiler.getMetricsWidth
import com.sofar.profiler.getStatusBarHeight

class FloatingWidget(context: Context) : FrameLayout(context) {

  private val TAG = "FloatingWidget"

  private val dragHelper: DragHelper
  private val screenWidth: Int = getMetricsWidth(context)
  private val screenHeight: Int = getMetricsHeight(context)
  private val statusBarHeight: Int = getStatusBarHeight(context)

  private val insets = Rect()
  private var xRatio: Float = 0f
  private var yRatio: Float = 0f

  private val dragCallback = DragCallback()

  private var viewWidth: Int = 0
  private var viewHeight: Int = 0

  init {
    dragHelper = DragHelper.create(this, dragCallback)
  }

  fun attach() {
    if (isAttached()) return

    (context as? Activity)?.let { activity ->
      val parent = activity.findViewById<ViewGroup>(android.R.id.content)
      attach(parent)
    }
  }

  fun attach(parent: ViewGroup) {
    if (isAttached()) return

    val lp = ViewGroup.LayoutParams(
      if (viewWidth > 0 || viewWidth == ViewGroup.LayoutParams.MATCH_PARENT) viewWidth else ViewGroup.LayoutParams.WRAP_CONTENT,
      if (viewHeight > 0 || viewHeight == ViewGroup.LayoutParams.MATCH_PARENT) viewHeight else ViewGroup.LayoutParams.WRAP_CONTENT
    )
    parent.addView(this, lp)
  }

  fun isAttached(): Boolean = parent != null

  fun detach() {
    (parent as? ViewGroup)?.removeView(this)
  }

  fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    insets.set(left, top, right, bottom)
  }

  fun setSize(width: Int, height: Int) {
    this.viewWidth = width
    this.viewHeight = height
  }

  fun setScreenRatio(x: Float, y: Float) {
    this.xRatio = x
    this.yRatio = y
  }

  private fun applyScreenRatio() {
    if (width == 0 || height == 0) return

    val x = xRatio * screenWidth
    val y = yRatio * screenHeight
    moveTo(x, y)
  }

  private fun moveTo(x: Float, y: Float) {
    this.x = dragCallback.clampX(x.toInt()).toFloat()
    this.y = dragCallback.clampY(y.toInt()).toFloat()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    applyScreenRatio()
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    return super.dispatchTouchEvent(ev)
  }

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    parent?.requestDisallowInterceptTouchEvent(true)
    return dragHelper.shouldInterceptTouchEvent(ev)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    return dragHelper.processTouchEvent(event)
  }

  inner class DragCallback : DragHelper.Callback {
    override fun onDragStart() {
      Log.d(TAG, "onDragStart")
    }

    override fun onDragEnd() {
      Log.d(TAG, "onDragEnd")
    }

    override fun onDragOffset(dx: Int, dy: Int) {
      Log.d(TAG, "onDragOffset")
    }

    override fun clampX(x: Int): Int {
      return x.coerceIn(insets.left, screenWidth - insets.right - width)
    }

    override fun clampY(y: Int): Int {
      return y.coerceIn(insets.top, screenHeight - statusBarHeight - insets.bottom - height)
    }
  }
}