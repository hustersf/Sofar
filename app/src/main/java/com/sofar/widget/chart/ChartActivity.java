package com.sofar.widget.chart;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.chart.charts.LineChart;
import com.sofar.chart.components.AxisBase;
import com.sofar.chart.components.XAxis;
import com.sofar.chart.data.Entry;
import com.sofar.chart.data.LineData;
import com.sofar.chart.data.LineDataSet;
import com.sofar.chart.formatter.IAxisValueFormatter;
import com.sofar.chart.highlight.Highlight;
import com.sofar.chart.interfaces.datasets.ILineDataSet;
import com.sofar.chart.listener.OnChartValueSelectedListener;
import com.sofar.chart.utils.Utils;

public class ChartActivity extends AppCompatActivity {

  private LineChart mLineChartView1;
  private LineChart mLineChartView2;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chart_activity);
    setTitle("图表测试");

    defaultLineChart();
    lineChart();
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
        String ss = data1.get(index).getY() + " : " + data2.get(index).getY();
        resultTv.setText("数值=" + ss);
      }

      @Override
      public void onNothingSelected() {

      }
    });
  }

}
