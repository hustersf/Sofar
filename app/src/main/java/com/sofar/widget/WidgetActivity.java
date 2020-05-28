package com.sofar.widget;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.base.span.SpanUtil;
import com.sofar.utility.DeviceUtil;
import com.sofar.widget.dialog.QueueDialogFragment1;
import com.sofar.widget.dialog.QueueDialogFragment2;
import com.sofar.widget.dialog.QueueDialogFragment3;
import com.sofar.widget.dialog.SofarDialogQueue;

public class WidgetActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("控件测试页面");
    setContentView(R.layout.widget_activity);

    QueueDialogFragment1 d1 = new QueueDialogFragment1();
    QueueDialogFragment2 d2 = new QueueDialogFragment2();
    QueueDialogFragment3 d3 = new QueueDialogFragment3();
    TextView dialog = findViewById(R.id.dialog);
    dialog.setOnClickListener(v -> {

      SofarDialogQueue.get().show(this, d1);
      SofarDialogQueue.get().show(this, d2);
      SofarDialogQueue.get().show(this, d3);
    });

    span1();
    span2();
  }

  private void span1() {
    int padding = DeviceUtil.dp2px(this, 0);
    TextView span = findViewById(R.id.span1);
    String text = "我的图片小，文字大，图片和文字之间无间距";
    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
    drawable.setBounds(0, 0, DeviceUtil.dp2px(this, 28), DeviceUtil.dp2px(this, 10));
    SpannableString spannableString = SpanUtil.getLeftImageSpan(text, drawable, padding);
    span.setText(spannableString);
  }

  private void span2() {
    int padding = DeviceUtil.dp2px(this, 10);
    TextView span = findViewById(R.id.span2);
    String text = "我的文字大，图片小，图片和文字之间有间距";
    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
    drawable.setBounds(0, 0, DeviceUtil.dp2px(this, 28), DeviceUtil.dp2px(this, 28));
    SpannableString spannableString = SpanUtil.getLeftImageSpan(text, drawable, padding);
    span.setText(spannableString);
  }
}
