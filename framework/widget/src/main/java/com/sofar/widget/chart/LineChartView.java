package com.sofar.widget.chart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.widget.Util;
import com.sofar.widget.chart.component.XAxis;
import com.sofar.widget.chart.component.YAxis;
import com.sofar.widget.chart.model.Entry;
import com.sofar.widget.chart.model.LineData;
import com.sofar.widget.chart.model.LineDataSet;

/**
 * 折线图/曲线图
 */
public class LineChartView extends View {

  private XAxis mXAxis;
  private YAxis mYAxis;
  private LineData mData;

  private Paint mXAxisLinePaint = new Paint();
  private Paint mXGirdLinePaint = new Paint();
  private int mDataSize;

  private Paint mYAxisLinePaint = new Paint();
  private Paint mYGirdLinePaint = new Paint();
  private Paint mYLabelPaint = new Paint();
  private float mYLabelWidth;
  private float mYScale;

  private Paint mLinePaint = new Paint();
  private Path mLinePath = new Path();

  private int mCanvasWidth;
  private int mCanvasHeight;

  public LineChartView(Context context) {
    this(context, null);
  }

  public LineChartView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
    testData();
  }

  private void init() {
    mXAxis = new XAxis();
    mYAxis = new YAxis();
  }

  public void setData(@NonNull LineData data) {
    mData = data;
    for (LineDataSet set : data.mDataSets) {
      mDataSize = Math.max(set.getSize(), mDataSize);
    }
    notifyDataChanged();
  }

  private void notifyDataChanged() {
    setXAxisPaint();
    setYAxisPaint();
  }

  private void setXAxisPaint() {
    mXAxisLinePaint.reset();
    mXAxisLinePaint.setColor(mXAxis.mAxisLineColor);
    mXAxisLinePaint.setStrokeWidth(mXAxis.mAxisLineWidth);
    mXAxisLinePaint.setStyle(Paint.Style.FILL);
    if (mXAxis.mAxisLineDashPathEffect != null) {
      mXAxisLinePaint.setPathEffect(mXAxis.mAxisLineDashPathEffect);
    }
    mXGirdLinePaint.reset();
    mXGirdLinePaint.setColor(mXAxis.mGridColor);
    mXGirdLinePaint.setStrokeWidth(mXAxis.mGridLineWidth);
    mXGirdLinePaint.setStyle(Paint.Style.FILL);
    if (mXAxis.mGridDashPathEffect != null) {
      mXGirdLinePaint.setPathEffect(mXAxis.mGridDashPathEffect);
    }
  }

  private void setYAxisPaint() {
    mYAxisLinePaint.reset();
    mYAxisLinePaint.setColor(mYAxis.mAxisLineColor);
    mYAxisLinePaint.setStrokeWidth(mYAxis.mAxisLineWidth);
    mYAxisLinePaint.setStyle(Paint.Style.FILL);
    if (mYAxis.mAxisLineDashPathEffect != null) {
      mYAxisLinePaint.setPathEffect(mYAxis.mAxisLineDashPathEffect);
    }
    mYGirdLinePaint.reset();
    mYGirdLinePaint.setColor(mYAxis.mGridColor);
    mYGirdLinePaint.setStrokeWidth(mYAxis.mGridLineWidth);
    mYGirdLinePaint.setStyle(Paint.Style.FILL);
    if (mYAxis.mGridDashPathEffect != null) {
      mYGirdLinePaint.setPathEffect(mYAxis.mGridDashPathEffect);
    }

    mYLabelPaint.reset();
    mYLabelPaint.setColor(mYAxis.mAxisLineColor);
    mYLabelPaint.setTextSize(Util.dp2px(getContext(), mYAxis.mAxisLabelTextSize));
    mYLabelWidth = Math.max(mYLabelPaint.measureText(mYAxis.mAxisMaximum + ""),
      mYLabelPaint.measureText(mYAxis.mAxisMinimum + ""));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = measureWidth(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  private int measureWidth(int measureSpec) {
    int result = 0;
    int mode = MeasureSpec.getMode(measureSpec);
    int size = MeasureSpec.getSize(measureSpec);
    if (mode == MeasureSpec.EXACTLY) {
      result = size;
      mXAxis.mItemWidth = (result - extraWidth()) / (mDataSize - 1);
    } else {
      result = (int) (mXAxis.mItemWidth * (mDataSize - 1) + extraWidth());
    }
    return result;
  }

  private float extraWidth() {
    return mYLabelWidth + getPaddingLeft() + getPaddingRight();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    mCanvasWidth = getWidth() - getPaddingLeft() - getPaddingRight();
    mCanvasHeight = getHeight() - getPaddingTop() - getPaddingBottom();
    mYScale = mCanvasHeight / mYAxis.mAxisRange;

    canvas.save();
    canvas.translate(getPaddingLeft(), getPaddingTop());

    drawXAxis(canvas);
    drawYAxis(canvas);
    drawLines(canvas);

    canvas.restore();
  }

  private void drawXAxis(Canvas canvas) {
    float startX = 0;
    float startY = mXAxisLinePaint.getStrokeWidth() / 2;
    float stopX = mCanvasWidth;
    float stopY = startY;
    if (mXAxis.mPosition == XAxis.XAxisPosition.BOTTOM) {
      startX = 0;
      startY = mCanvasHeight - mXAxisLinePaint.getStrokeWidth() / 2;
      stopX = mCanvasWidth;
      stopY = startY;
    }
    canvas.drawLine(startX, startY, stopX, stopY, mXAxisLinePaint);
  }

  private void drawYAxis(Canvas canvas) {
    float startX = mYAxisLinePaint.getStrokeWidth() / 2 + mYLabelWidth;
    float startY = 0;
    float stopX = startX;
    float stopY = mCanvasHeight;
    if (mYAxis.mPosition == YAxis.YAxisPosition.RIGHT) {
      startX = mCanvasWidth - mYAxisLinePaint.getStrokeWidth() / 2 - mYLabelWidth;
      startY = 0;
      stopX = startX;
      stopY = mCanvasHeight;
    }
    canvas.drawLine(startX, startY, stopX, stopY, mYAxisLinePaint);


    float value = mYAxis.mAxisMinimum;
    float space = mYAxis.mAxisRange / (mYAxis.mLabelCount - 1);
    for (int i = 0; i < mYAxis.mLabelCount; i++) {
      canvas.drawText(value + "", startX, yAxisConvert(value), mYLabelPaint);
      value += space;
    }
  }

  /**
   * 将数据转化成对应的坐标
   */
  private int yAxisConvert(float value) {
    return (int) (mCanvasHeight - (value - mYAxis.mAxisMinimum) * mYScale);
  }


  private void drawLines(Canvas canvas) {
    for (int i = 0; i < mData.mDataSets.size(); i++) {
      LineDataSet dataSet = mData.mDataSets.get(i);
      mLinePaint.reset();
      mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      mLinePaint.setColor(dataSet.mLineColor);
      mLinePaint.setStrokeWidth(dataSet.mLineWidth);
      mLinePaint.setStyle(Paint.Style.STROKE);
      mLinePath.reset();

      if (dataSet.mMode == LineDataSet.Mode.LINEAR) {
        drawLine(canvas, dataSet);
      } else {
        drawCubicLine(canvas, dataSet);
      }
    }
  }

  private void drawLine(Canvas canvas, LineDataSet dataSet) {
    List<Entry> entries = dataSet.mEntries;

    float x = 0;
    float y = yAxisConvert(entries.get(0).y);
    mLinePath.moveTo(x, y);
    for (int i = 1; i < entries.size(); i++) {
      x = mXAxis.mItemWidth * i;
      y = yAxisConvert(entries.get(i).y);
      mLinePath.lineTo(x, y);
    }
    canvas.drawPath(mLinePath, mLinePaint);
  }

  private void drawCubicLine(Canvas canvas, LineDataSet dataSet) {
    List<Entry> entries = dataSet.mEntries;
    Entry cur = entries.get(0);
    float x = 0;
    float y = yAxisConvert(cur.y);
    mLinePath.moveTo(x, y);
    for (int i = 1; i < entries.size() - 1; i++) {
      cur = entries.get(i);
      x = mXAxis.mItemWidth * i;
      y = yAxisConvert(cur.y);

      Entry next = entries.get(i + 1);
      float nextX = mXAxis.mItemWidth * (i + 1);
      float nextY = yAxisConvert(next.y);

      float x1 = (x + nextX) / 2;
      float y1 = y;
      float x2 = (x + nextX) / 2;
      float y2 = nextY;
      float x3 = nextX;
      float y3 = nextY;
      mLinePath.cubicTo(x1, y1, x2, y2, x3, y3);
    }
    canvas.drawPath(mLinePath, mLinePaint);
  }

  public XAxis getXAxis() {
    return mXAxis;
  }

  public YAxis getYAxis() {
    return mYAxis;
  }

  private void testData() {

    List<Entry> entries = new ArrayList<>();
    int x = 10;
    int y = 200;
    for (int i = 0; i < x; i++) {
      float val = (float) (Math.random() * y) - 30;
      if (i == 5) {
        val = -30;
      }
      entries.add(new Entry(i, val));
    }

    this.getYAxis().setAxisMinimum(-30);
    this.getYAxis().setAxisMaximum(200);
    this.getYAxis().setPosition(YAxis.YAxisPosition.RIGHT);
    LineDataSet set1 = new LineDataSet(entries, "dataSet1");
    List<LineDataSet> sets = new ArrayList<>();
    sets.add(set1);
    LineData data = new LineData(sets);
    setData(data);
  }

}
