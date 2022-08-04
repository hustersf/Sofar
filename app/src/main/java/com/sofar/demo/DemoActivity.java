package com.sofar.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.aidl.AIDLActivity;

public class DemoActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo_activity);
    aidl();
  }

  private void aidl() {
    Button button = findViewById(R.id.aidl_btn);
    button.setOnClickListener(v -> {
      Intent intent = new Intent(this, AIDLActivity.class);
      startActivity(intent);
    });
  }
}
