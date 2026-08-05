package com.sofar.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.Nullable;

public class AIDLService extends Service {

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return mIBinder;
  }

  private IBinder mIBinder = new IMyAidlInterface.Stub() {
    @Override
    public int add(int a, int b) throws RemoteException {
      return a + b;
    }
  };
}
