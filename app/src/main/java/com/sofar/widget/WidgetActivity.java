package com.sofar.widget;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
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
  }
}
