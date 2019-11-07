package com.sofar.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

/**
 * 获取网络信息
 */
public class NetworkUtil {

  public static boolean isNetwork(@NonNull Context context){
    boolean available = false;
    ConnectivityManager nManager = (ConnectivityManager) context
      .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = nManager.getActiveNetworkInfo();
    if (networkInfo != null) {
      available = networkInfo.isAvailable();
    }
    return available;
  }

}
