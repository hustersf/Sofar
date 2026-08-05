package com.sofar.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ClickReceiver extends BroadcastReceiver {

  public static String CLICK_ACTION = "clock_click_action";

  @Override
  public void onReceive(Context context, Intent intent) {
    Toast.makeText(context, "点击事件", Toast.LENGTH_SHORT).show();
  }
}
