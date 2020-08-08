package com.sofar.fun;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.fun.dialog.QueueDialogFragment1;
import com.sofar.fun.dialog.QueueDialogFragment2;
import com.sofar.fun.dialog.QueueDialogFragment3;
import com.sofar.fun.dialog.SofarDialogQueue;

public class FunActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("有趣的");
    setContentView(R.layout.fun_activity);

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
