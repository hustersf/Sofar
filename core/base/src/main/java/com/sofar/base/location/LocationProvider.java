package com.sofar.base.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationProvider {

  private static final String TAG = "LocationProvider";
  private final long MIN_TIME = 5000;
  private final long MIN_DISTANCE = 10;

  private String[] providers = {LocationManager.GPS_PROVIDER,
    LocationManager.NETWORK_PROVIDER,
    LocationManager.PASSIVE_PROVIDER};

  @NonNull
  private LocationManager locationManager;
  @NonNull
  private Context appContext;
  private boolean init = false;

  private LocationProvider() {
  }

  private static class HolderClass {
    private static LocationProvider sInstance = new LocationProvider();
  }

  public static LocationProvider getInstance() {
    return LocationProvider.HolderClass.sInstance;
  }

  public void init(@NonNull Context context) {
    if (init) {
      return;
    }
    appContext = context.getApplicationContext();
    locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    init = true;
  }


  /**
   * 获取定位
   */
  @Nullable
  public Location getLocation() {
    checkInit();

    if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
      && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "未打开定位权限");
      return null;
    }

    for (String provider : providers) {
      Location location = locationManager.getLastKnownLocation(provider);
      if (location != null) {
        Log.d(TAG, location.toString());
        return location;
      }
    }
    return null;
  }

  /**
   * 开启定位
   */
  public void startLocation() {
    checkInit();
    if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
      && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "未打开定位权限");
      return;
    }

    for (String provider : providers) {
      if (locationManager.isProviderEnabled(provider)) {
        Log.d(TAG, "start " + provider + " location");
        locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, locationListener);
      }
    }
  }

  private void checkInit() {
    if (!init) {
      throw new IllegalArgumentException("LocationProvider must be init first");
    }
  }


  LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      Log.d(TAG, "onLocationChanged:" + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Log.d(TAG, "onStatusChanged:provider=" + provider + " status=" + status + " extras=" + extras.toString());
    }

    @Override
    public void onProviderEnabled(String provider) {
      Log.d(TAG, "onProviderEnabled:" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
      Log.d(TAG, "onProviderDisabled:" + provider);
    }
  };

}
