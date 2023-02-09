package com.sofar.utility.calendar;

import java.util.TimeZone;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

/**
 * 系统日历添加和删除信息
 * <p>
 * 读写日历需要对应的权限
 * 建议业务方异步调用相关方法
 */
public class CalendarRemindUtil {
  private static final String ACCOUNT_NAME = "我的日历";

  /**
   * 插入一个日历事件,参数说明详见下面的方法
   */
  public static long insertCalendarEvent(Context context, String title, String description,
    long beginTime, long durationSeconds, int remindMinutes) {
    return insertCalendarEventRepeatByDay(context, title, description, beginTime, durationSeconds,
      remindMinutes, 1);
  }

  /**
   * @param context
   * @param title
   * @param description
   * @param beginTime       日历提醒时间
   * @param durationSeconds 日历提醒持续多少秒, 比如3600代表一小时
   * @param remindMinutes   日历提前几分钟开始提醒
   * @param count           循环添加多少天
   * @return 返回插入事件的id
   */
  public static long insertCalendarEventRepeatByDay(Context context, String title,
    String description, long beginTime, long durationSeconds, int remindMinutes, int count) {
    long calendar_id = checkAndAddCalendarAccounts(context);
    if (calendar_id < 0) {
      return -1;
    }

    ContentValues event = new ContentValues();
    event.put(CalendarContract.Events.TITLE, title);
    event.put(CalendarContract.Events.DESCRIPTION, description);
    event.put(CalendarContract.Events.CALENDAR_ID, calendar_id); // 插入账户的id
    event.put(CalendarContract.Events.DTSTART, beginTime);// 必须有
    event.put(CalendarContract.Events.DURATION, String.format("P%sS", durationSeconds));
    if (count > 1) {
      event.put(CalendarContract.Events.RRULE, String.format("FREQ=DAILY;COUNT=%s", count));
    }
    event.put(CalendarContract.Events.HAS_ALARM, 1);// 设置有闹钟提醒
    event.put(CalendarContract.Events.ALL_DAY, 0);
    event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());// 这个是时区，必须有

    try {
      //插入事件
      Uri insertEvent =
        context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
      long event_id = ContentUris.parseId(insertEvent);
      ContentValues values = new ContentValues();
      values.put(CalendarContract.Reminders.EVENT_ID, event_id);
      // 提前remind_minutes分钟有提醒
      values.put(CalendarContract.Reminders.MINUTES, remindMinutes);
      //提醒方式
      values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
      //插入提醒
      context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
      return event_id;
    } catch (Exception e) {
      //可以打印log
      return -1;
    }
  }

  /**
   * 按标题维度删除日历
   *
   * @return 删除成功or失败
   */
  public static boolean deleteCalendarEvent(Context context, String title) {
    Cursor eventCursor = null;
    try {
      eventCursor = context.getContentResolver()
        .query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
      if (eventCursor == null) { // 查询返回空值
        return false;
      }
      if (eventCursor.getCount() > 0) {
        // 遍历所有事件，找到title、跟需要查询的title一样的项
        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
          int titleIndex = eventCursor.getColumnIndex(CalendarContract.Events.TITLE);
          String eventTitle = eventCursor.getString(titleIndex);
          if (TextUtils.equals(title, eventTitle)) {
            int idIndex = eventCursor.getColumnIndex(CalendarContract.Calendars._ID);
            int id = eventCursor.getInt(idIndex);// 取得id
            Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
            context.getContentResolver().delete(deleteUri, null, null);
          }
        }
      }
    } catch (Exception e) {
      //可以打印log
      return false;
    } finally {
      if (eventCursor != null) {
        eventCursor.close();
      }
    }
    return true;
  }


  /**
   * 检查是否有日历账户, 如果没有添加一个
   *
   * @param context
   * @return 账户id
   */
  private static int checkAndAddCalendarAccounts(Context context) {
    int oldId = checkCalendarAccounts(context);
    if (oldId >= 0) {
      return oldId;
    } else {
      long addId = addCalendarAccount(context);
      if (addId >= 0) {
        return checkCalendarAccounts(context);
      } else {
        return -1;
      }
    }
  }

  private static int checkCalendarAccounts(Context context) {
    try (Cursor userCursor = context.getContentResolver()
      .query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null)) {
      if (userCursor == null) {
        return -1;
      }
      int count = userCursor.getCount();
      int columnIndex = userCursor.getColumnIndex(CalendarContract.Calendars._ID);
      if (count > 0) {
        // 存在现有账户，取第一个账户的id返回
        userCursor.moveToFirst();
        return userCursor.getInt(columnIndex);
      } else {
        return -1;
      }
    } catch (Exception e) {
      //可以打印日志
      return -1;
    }
  }

  /**
   * 添加一个日历账户
   */
  private static long addCalendarAccount(Context context) {
    ContentValues value = new ContentValues();
    value.put(CalendarContract.Calendars.NAME, ACCOUNT_NAME);
    value.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
    value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, ACCOUNT_NAME);
    value.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
    value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
    value.put(CalendarContract.Calendars.VISIBLE, 1);
    value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
    value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
      CalendarContract.Calendars.CAL_ACCESS_OWNER);
    value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
    value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
    value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

    Uri calendarUri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
      .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
      .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
      .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
        CalendarContract.Calendars.CALENDAR_LOCATION).build();

    try {
      Uri accountUri = context.getContentResolver().insert(calendarUri, value);
      return accountUri == null ? -1 : ContentUris.parseId(accountUri);
    } catch (Exception e) {
      //打印log
      return -1;
    }
  }

}
