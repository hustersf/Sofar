package com.sofar.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;

/**
 * 获取网络信息
 */
public class NetworkUtil {

  /**
   * 网络是否可用
   */
  public static boolean isNetworkAvailable(@NonNull Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
      if (capabilities != null) {
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
          && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
      } else {
        return false;
      }
    } else {
      boolean available = false;
      NetworkInfo networkInfo = manager.getActiveNetworkInfo();
      if (networkInfo != null) {
        available = networkInfo.isAvailable();
      }
      return available;
    }
  }

  /**
   * 数据是否可用
   */
  public static boolean isMobileAvailable(@NonNull Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
      if (capabilities != null) {
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
          && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
          && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
      } else {
        return false;
      }
    } else {
      boolean available = false;
      NetworkInfo networkInfo = manager.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        available = networkInfo.isAvailable();
      }
      return available;
    }
  }

  /**
   * wifi是否可用
   */
  public static boolean isWifiAvailable(@NonNull Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
      if (capabilities != null) {
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
          && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
          && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
      } else {
        return false;
      }
    } else {
      boolean available = false;
      NetworkInfo networkInfo = manager.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        available = networkInfo.isAvailable();
      }
      return available;
    }
  }

  /**
   * 获取IP地址
   */
  public static String getIP(@NonNull Context context) {
    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    int i = wifiInfo.getIpAddress();
    return int2ip(i);
  }

  private static String int2ip(int ipInt) {
    StringBuilder sb = new StringBuilder();
    sb.append(ipInt & 0xFF).append(".");
    sb.append((ipInt >> 8) & 0xFF).append(".");
    sb.append((ipInt >> 16) & 0xFF).append(".");
    sb.append((ipInt >> 24) & 0xFF);
    return sb.toString();
  }


}

