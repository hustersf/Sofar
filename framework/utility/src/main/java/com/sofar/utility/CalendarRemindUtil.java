package com.sofar.utility;

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
 */
public class CalendarRemindUtil {

  private static final String CALENDAR_URL = "content://com.android.calendar/calendars";
  private static final String CALENDAR_EVENT_URL = "content://com.android.calendar/events";
  private static final String CALENDAR_REMINDER_URL = "content://com.android.calendar/reminders";

  private static String CALENDARS_NAME = "日历名字";
  private static String CALENDARS_ACCOUNT_NAME = "日历账户名字";
  private static String CALENDARS_ACCOUNT_TYPE = "日历账户类型";
  private static String CALENDARS_DISPLAY_NAME = "日历展示名字";

  /**
   * @param context
   * @param title         标题
   * @param description   描述
   * @param beginTime     日历提醒时间
   * @param remindMinutes 日历提前几分钟开始提醒
   * @param count         循环添加多少天
   * @return 添加成功or失败
   */
  public static boolean addCalendarRecurEventRemind(Context context, String title,
    String description, long beginTime, int remindMinutes, int count) {
    long calendar_id = checkAndAddCalendarAccounts(context);
    if (calendar_id < 0) {
      return false;
    }

    if (TextUtils.isEmpty(title)) {
      return false;
    }
    deleteCalendarRemind(context, title, description);

    Uri newEvent =
      insertCalendarRecurEvent(context, calendar_id, title, description, beginTime, count);
    if (newEvent == null) {
      // 添加日历事件失败直接返回
      return false;
    }

    long event_id = ContentUris.parseId(newEvent);
    ContentValues values = new ContentValues();
    values.put(CalendarContract.Reminders.EVENT_ID, event_id);
    // 提前remind_minutes分钟有提醒
    values.put(CalendarContract.Reminders.MINUTES, remindMinutes);
    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
    Uri uri = context.getContentResolver().insert(Uri.parse(CALENDAR_REMINDER_URL), values);
    if (uri == null) {
      // 添加提醒失败直接返回
      return false;
    }

    return true;
  }

  /**
   * 按标题维度删除日历
   *
   * @return 删除成功or失败
   */
  public static boolean deleteCalendarRemind(Context context, String title, String description) {
    Cursor eventCursor = context.getContentResolver()
      .query(Uri.parse(CALENDAR_EVENT_URL), null, null, null,
        CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " ASC ");
    if (eventCursor == null) {
      return false;
    }

    try {
      if (eventCursor.getCount() > 0) {
        // 遍历所有事件，找到title、跟需要查询的title一样的项
        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
          String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
          if (TextUtils.equals(title, eventTitle)) {
            int id =
              eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));// 取得id
            Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), id);
            int rows = context.getContentResolver().delete(deleteUri, null, null);
          }
        }
      }
    } finally {
      eventCursor.close();
    }
    return true;
  }


  /**
   * 向日历中添加一个循环事件
   *
   * @param context
   * @param calendar_id （必须参数）
   * @param title
   * @param description
   * @param beginTime   事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。 （必须参数）
   * @param count       按天算循环添加多少次
   * @return
   */
  private static Uri insertCalendarRecurEvent(Context context, long calendar_id, String title,
    String description, long beginTime, int count) {
    ContentValues event = new ContentValues();
    event.put(CalendarContract.Events.TITLE, title);
    event.put(CalendarContract.Events.DESCRIPTION, description);
    event.put(CalendarContract.Events.CALENDAR_ID, calendar_id); // 插入账户的id
    event.put(CalendarContract.Events.DTSTART, beginTime);// 必须有
    event.put(CalendarContract.Events.DURATION, "P3600S");
    event.put(CalendarContract.Events.RRULE, String.format("FREQ=DAILY;COUNT=%s", count));
    event.put(CalendarContract.Events.HAS_ALARM, 1);// 设置有闹钟提醒
    event.put(CalendarContract.Events.ALL_DAY, 0);
    event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());// 这个是时区，必须有，
    Uri newEvent =
      context.getContentResolver().insert(Uri.parse(CALENDAR_EVENT_URL), event); // 添加事件
    return newEvent;
  }

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
    Cursor userCursor =
      context.getContentResolver().query(Uri.parse(CALENDAR_URL), null, null, null,
        CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " DESC ");
    try {
      if (userCursor == null) {
        return -1;
      }
      int count = userCursor.getCount();
      if (count > 0) {
        // 存在现有账户，取第一个账户的id返回
        userCursor.moveToFirst();
        return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
      } else {
        return -1;
      }
    } finally {
      if (userCursor != null) {
        userCursor.close();
      }
    }
  }

  /**
   * 添加一个日历账户
   */
  private static long addCalendarAccount(Context context) {
    TimeZone timeZone = TimeZone.getDefault();
    ContentValues value = new ContentValues();
    value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

    value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
    value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
    value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
    value.put(CalendarContract.Calendars.VISIBLE, 1);
    value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
    value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
      CalendarContract.Calendars.CAL_ACCESS_OWNER);
    value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
    value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
    value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
    value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

    Uri calendarUri = Uri.parse(CALENDAR_URL);
    calendarUri = calendarUri.buildUpon()
      .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
      .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
      .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
      .build();

    Uri result = context.getContentResolver().insert(calendarUri, value);
    long id = result == null ? -1 : ContentUris.parseId(result);
    return id;
  }

}
