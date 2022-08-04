package com.sofar.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AIDLActivity extends AppCompatActivity {

  private static final String TAG = "AIDLActivity";

  IMyAidlInterface mAidlInterface;
  ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mAidlInterface = IMyAidlInterface.Stub.asInterface(service);
      Log.d(TAG, "onServiceConnected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mAidlInterface = null;
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.aidl_activity);
    bindService();
    aidl();
  }

  //启动服务
  private void bindService() {
    Intent intent = new Intent(this, AIDLService.class);
    this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  private void aidl() {
    Button button = findViewById(R.id.aidl_btn);
    button.setOnClickListener(v -> {
      try {
        int result = mAidlInterface.add(12, 12);
        Log.d(TAG, "aidl result=" + result);
      } catch (RemoteException e) {
        e.printStackTrace();
      }

    });
  }
}
