package com.sofar.widget.chart;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.chart.charts.BarChart;
import com.sofar.chart.charts.LineChart;
import com.sofar.chart.components.AxisBase;
import com.sofar.chart.components.XAxis;
import com.sofar.chart.data.BarData;
import com.sofar.chart.data.BarDataSet;
import com.sofar.chart.data.BarEntry;
import com.sofar.chart.data.Entry;
import com.sofar.chart.data.LineData;
import com.sofar.chart.data.LineDataSet;
import com.sofar.chart.formatter.IAxisValueFormatter;
import com.sofar.chart.highlight.Highlight;
import com.sofar.chart.interfaces.datasets.IBarDataSet;
import com.sofar.chart.interfaces.datasets.ILineDataSet;
import com.sofar.chart.listener.OnChartValueSelectedListener;
import com.sofar.chart.utils.MarkerViewHelper;
import com.sofar.chart.utils.Utils;

public class ChartActivity extends AppCompatActivity {

  private LineChart mLineChartView1;
  private LineChart mLineChartView2;

  private BarChart mBarChartView1;
  private BarChart mBarChartView2;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chart_activity);
    setTitle("图表测试");

    defaultLineChart();
    lineChart();

    defaultBarChart();
    barChart();
  }

  private void defaultLineChart() {
    mLineChartView1 = findViewById(R.id.line_chart1);

    int range = 240;
    List<Entry> entries = new ArrayList<>();
    for (int i = 0; i < 24; i++) {
      float val = (float) (Math.random() * range);
      entries.add(new Entry(i, val));
    }
    mLineChartView1.getAxisLeft().setAxisMaximum(240);
    mLineChartView1.getAxisLeft().setAxisMinimum(0);
    mLineChartView1.getAxisRight().setAxisMaximum(240);
    mLineChartView1.getAxisRight().setAxisMinimum(0);
    LineDataSet set1 = new LineDataSet(entries, "默认样式(x[0,23]:y[0.,240])");
    List<ILineDataSet> list = new ArrayList<>();
    list.add(set1);
    LineData lineData = new LineData(list);
    mLineChartView1.setData(lineData);
  }

  private void lineChart() {
    TextView resultTv = findViewById(R.id.line_chart2_result);
    mLineChartView2 = findViewById(R.id.line_chart2);
    mLineChartView2.getDescription().setEnabled(false);
    mLineChartView2.setScaleEnabled(false);
    MarkerViewHelper helper = new MarkerViewHelper(mLineChartView2, resultTv);

    int range = 80;
    List<Entry> data1 = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      float val = (float) (Math.random() * range) + 10;
      data1.add(new Entry(i, val));
    }
    List<Entry> data2 = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      float val = (float) (Math.random() * range) + 10;
      data2.add(new Entry(i, val));
    }

    float lineLength = Utils.convertDpToPixel(4);
    float spaceLength = Utils.convertDpToPixel(2);
    float axisLineWidth = 1f;
    mLineChartView2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    mLineChartView2.getXAxis().setLabelCount(4, false);
    mLineChartView2.getXAxis().setAxisLineColor(Color.BLUE);
    mLineChartView2.getXAxis().setAxisLineWidth(axisLineWidth);
    mLineChartView2.getXAxis().setGridColor(Color.BLUE);
    mLineChartView2.getXAxis().setGridLineWidth(axisLineWidth);
    mLineChartView2.getXAxis().enableGridDashedLine(lineLength, spaceLength, 0);
    mLineChartView2.setHighestVisibleX(15);

    mLineChartView2.getAxisRight().setEnabled(false);
    mLineChartView2.getAxisLeft().setAxisMaximum(100);
    mLineChartView2.getAxisLeft().setAxisMinimum(0);
    mLineChartView2.getAxisLeft().setTextColor(Color.GRAY);
    mLineChartView2.getAxisLeft().setDrawAxisLine(false);
    mLineChartView2.getAxisLeft().setAxisLineWidth(axisLineWidth);
    mLineChartView2.getAxisLeft().setAxisLineColor(Color.RED);
    mLineChartView2.getAxisLeft().setGridColor(Color.RED);
    mLineChartView2.getAxisLeft().setGridLineWidth(axisLineWidth);
    mLineChartView2.getAxisLeft().enableGridDashedLine(lineLength, spaceLength, 0);
    mLineChartView2.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
      @Override
      public String getFormattedValue(float value, AxisBase axis) {
        return (int) value + "W.h";
      }
    });
    mLineChartView2.getAxisLeft().setLabelCount(5, true);
    LineDataSet set1 = new LineDataSet(data1, "累计发电量");
    set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    set1.setColor(Color.CYAN);
    set1.setCircleColor(Color.CYAN);
    set1.setDrawHighlightCircle(true);
    set1.setDrawHorizontalHighlightIndicator(false);

    LineDataSet set2 = new LineDataSet(data2, "累计耗电量");
    set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    set2.setColor(Color.GREEN);
    set2.setCircleColor(Color.GREEN);
    set2.setDrawHighlightCircle(true);
    set2.setDrawHorizontalHighlightIndicator(false);

    List<ILineDataSet> list = new ArrayList<>();
    list.add(set1);
    list.add(set2);
    LineData lineData = new LineData(list);
    lineData.setDrawValues(false);
    mLineChartView2.setData(lineData);

    mLineChartView2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
      @Override
      public void onValueSelected(Entry e, Highlight h) {
        int index = (int) e.getX();
        resultTv.setText("[x=" + index + ",y1=" + data1.get(index).getY()
          + ",y2=" + data2.get(index).getY());
        helper.moveWithHighlight(e, h);
      }

      @Override
      public void onNothingSelected() {

      }
    });
  }

  private void defaultBarChart() {
    mBarChartView1 = findViewById(R.id.bar_chart1);

    int range = 240;
    List<BarEntry> entries = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      float val = (float) (Math.random() * range);
      entries.add(new BarEntry(i, val));
    }
    mBarChartView1.getAxisLeft().setAxisMaximum(240);
    mBarChartView1.getAxisLeft().setAxisMinimum(0);
    mBarChartView1.getAxisRight().setAxisMaximum(240);
    mBarChartView1.getAxisRight().setAxisMinimum(0);
    BarDataSet set1 = new BarDataSet(entries, "默认样式(x[0,23]:y[0.,240])");
    List<IBarDataSet> list = new ArrayList<>();
    list.add(set1);
    BarData barData = new BarData(list);
    mBarChartView1.setData(barData);
  }

  private void barChart() {
    TextView resultTv = findViewById(R.id.bar_chart2_result);
    mBarChartView2 = findViewById(R.id.bar_chart2);
    mBarChartView2.getDescription().setEnabled(false);
    mBarChartView2.setScaleEnabled(false);

    int range = 100;
    List<BarEntry> entries = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      float val = (float) (Math.random() * range);
      entries.add(new BarEntry(i, val));
    }

    float lineLength = Utils.convertDpToPixel(4);
    float spaceLength = Utils.convertDpToPixel(2);
    float axisLineWidth = 1f;

    mBarChartView2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    mBarChartView2.getXAxis().setLabelCount(4, false);
    mBarChartView2.getXAxis().setAxisLineColor(Color.BLUE);
    mBarChartView2.getXAxis().setAxisLineWidth(axisLineWidth);
    mBarChartView2.getXAxis().setGridColor(Color.BLUE);
    mBarChartView2.getXAxis().setGridLineWidth(axisLineWidth);
    mBarChartView2.getXAxis().setDrawFirstGridLine(false);
    mBarChartView2.getXAxis().enableGridDashedLine(lineLength, spaceLength, 0);

    mBarChartView2.getAxisRight().setEnabled(false);
    mBarChartView2.getAxisLeft().setAxisMaximum(100);
    mBarChartView2.getAxisLeft().setAxisMinimum(0);
    mBarChartView2.getAxisLeft().setTextColor(Color.GRAY);
    mBarChartView2.getAxisLeft().enableAxisLineDashedLine(lineLength, spaceLength, 0);
    mBarChartView2.getAxisLeft().setAxisLineWidth(axisLineWidth);
    mBarChartView2.getAxisLeft().setAxisLineColor(Color.RED);
    mBarChartView2.getAxisLeft().setGridColor(Color.RED);
    mBarChartView2.getAxisLeft().setGridLineWidth(axisLineWidth);
    mBarChartView2.getAxisLeft().enableGridDashedLine(lineLength, spaceLength, 0);
    mBarChartView2.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
      @Override
      public String getFormattedValue(float value, AxisBase axis) {
        return (int) value + "%";
      }
    });
    BarDataSet set1 = new BarDataSet(entries, "电池平均电量");
    set1.setDrawFullCorners(true);
    set1.setMode(BarDataSet.Mode.TOP_ROUND);
    List<IBarDataSet> list = new ArrayList<>();
    list.add(set1);
    BarData barData = new BarData(list);
    barData.setDrawValues(false);
    mBarChartView2.setData(barData);
    mBarChartView2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
      @Override
      public void onValueSelected(Entry e, Highlight h) {
        int index = (int) e.getX();
        resultTv.setText("数值=" + entries.get(index));
      }

      @Override
      public void onNothingSelected() {

      }
    });
  }

}
