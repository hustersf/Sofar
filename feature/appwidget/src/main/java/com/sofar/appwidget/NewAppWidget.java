package com.sofar.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * appwidget 使用demo
 * <p>
 * appWidgetIds 数组的理解
 * 一个 AppWidgetProvider 可以对应 多个 AppWidget. 但一般使用时都是一对一
 * <p>
 * AppWidget 几个角色
 * 提供方：app
 * 显示方：Launcher(系统应用)
 * <p>
 * AppWidgetManager:负责widget视图的实际更新以及相关管理
 * <p>
 * 这其中涉及到了跨进程通信
 * RemoteViews
 * 在 Android中 的使用场景主要有: 自定义通知栏和桌面小部件。
 * 实际上 RemoteViews 并不是显示的真正view, 它只是携带了一些 view 的信息给 系统进程
 * AppWidgetService
 * 在 SystemServer 进程中启动的一个系统服务
 * <p>
 * 总体流程解析
 * 1.AppWidgetService 启动
 * SystemServer 启动 AppWidgetService 系统服务
 * 2.AppWidgetProviderInfo 获取
 * 通过 PMS 服务解析 Manifest 文件 找到 注册了 android.appwidget.action.APPWIDGET_UPDATE 的广播
 * 然后解析 meta-data 中的 xml 文件转化成 AppWidgetProviderInfo
 * 3.Launcher 获取 Widget 信息
 * Launcher 通过 AppWidgetManager 向 AppWidgetService 按需拿到所有的 AppWidget 信息,可以进行展示。
 * 4.Launcher 显示 Widget 信息
 * Launcher 创建 AppWidgetHost，通过上面拿到的 Widget 信息生成对应的 AppWidgetHostView 进行展示。
 * 5.Launcher 更新Widget信息
 * AppWidgetHost 创建监听 AppWidgetService 的更新，进行接收回调显示更新。
 * 6.AppWidget被动刷新
 * AppWidgetService 会根据 AppWidgetProviderInfo 的配置维持一个30分钟下限的更新时钟,来给AppWidgetProvider
 * 来发送更新通知。
 * 7.AppWidget主动刷新
 * 应用侧可以拿到 AppWidgetManager 来进行主动刷新。
 */
public class NewAppWidget extends AppWidgetProvider {

  /**
   * 应用内直接添加组件到桌面
   */
  public static void requestPinAppWidget(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      AppWidgetManager appWidgetManager = context.getSystemService(AppWidgetManager.class);
      if (appWidgetManager != null) {
        ComponentName myProvider = new ComponentName(context, NewAppWidget.class);
        appWidgetManager.requestPinAppWidget(myProvider, null, null);
      }
    }
  }

  private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
    int appWidgetId) {
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_clock);
    Intent intent = new Intent(context, ClickReceiver.class);
    PendingIntent pendingIntent =
      PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    views.setOnClickPendingIntent(R.id.clock, pendingIntent);
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
    super.onRestored(context, oldWidgetIds, newWidgetIds);
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
    int appWidgetId, Bundle newOptions) {
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
  }

  /**
   * 因为用户可以对桌面小组件操作, 桌面应用属于别的进程
   * 这个进程通过广播 通知到 应用侧
   * <p>
   * 方法内部会根据不同的 ACTION 分发到 上面的几个方法
   * 由于是广播,意味着方法中不能有耗时操作,否则会 ANR
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
  }
}