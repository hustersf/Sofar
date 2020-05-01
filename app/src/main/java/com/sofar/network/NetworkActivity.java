package com.sofar.network;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.network.response.ResponseFunction;

public class NetworkActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("网络测试页面");
    setContentView(R.layout.network_activity);

    View bodyLayout = findViewById(R.id.body_layout);
    View dataLayout = findViewById(R.id.data_layout);
    TextView body = findViewById(R.id.body);
    TextView data = findViewById(R.id.data);

    bodyLayout.setOnClickListener(v -> {
      ApiProvider.getApiService().getBannerData()
        .subscribe(s -> {
          body.setText(s);
        }, throwable -> {
          body.setText(throwable.getMessage());
        });

    });

    dataLayout.setOnClickListener(v -> {
      ApiProvider.getApiService().getBannerDataResponse()
        .map(new ResponseFunction<>())
        .subscribe(s -> {
          data.setText(s);
        }, throwable -> {
          data.setText(throwable.getMessage());
        });
    });

  }

  private void clear(TextView textView) {
    textView.postDelayed(() -> {
      textView.setText("");
    }, 5000);
  }
}
