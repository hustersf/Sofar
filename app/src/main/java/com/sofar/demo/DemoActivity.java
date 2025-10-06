package com.sofar.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.aidl.AIDLActivity;
import com.sofar.datastore.DataStoreActivity;
import com.sofar.room.RoomActivity;

public class DemoActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo_activity);
    aidl();
    datastore();
    room();
  }

  private void aidl() {
    Button button = findViewById(R.id.aidl_btn);
    button.setOnClickListener(v -> {
      Intent intent = new Intent(this, AIDLActivity.class);
      startActivity(intent);
    });
  }

  private void datastore() {
    Button button = findViewById(R.id.datastore_btn);
    button.setOnClickListener(v -> {
      Intent intent = new Intent(this, DataStoreActivity.class);
      startActivity(intent);
    });
  }

  private void room() {
    Button button = findViewById(R.id.room_btn);
    button.setOnClickListener(v -> {
      Intent intent = new Intent(this, RoomActivity.class);
      startActivity(intent);
    });
  }
}
