package com.sofar.utility;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

/**
 * 日期工具类
 */
public class DateUtil {

  public static final SimpleDateFormat DEFAULT_DATE_FORMAT =
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private DateUtil() {
    throw new AssertionError();
  }

  /**
   * long time to string
   *
   * @param timeInMillis
   * @param dateFormat
   * @return
   */
  public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
    return dateFormat.format(new Date(timeInMillis));
  }

  /**
   * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
   *
   * @param timeInMillis
   * @return
   */
  public static String getTime(long timeInMillis) {
    return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
  }

  /**
   * get current time in milliseconds
   *
   * @return
   */
  public static long getCurrentTimeInLong() {
    return System.currentTimeMillis();
  }

  /**
   * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
   *
   * @return
   */
  public static String getCurrentTimeInString() {
    return getTime(getCurrentTimeInLong());
  }

  /**
   * get current time in milliseconds
   *
   * @return
   */
  public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
    return getTime(getCurrentTimeInLong(), dateFormat);
  }

  /**
   * 获取距当前时间 n天的时间
   */
  public static long getCurrentTimeAfterDays(int day) {
    return System.currentTimeMillis() + day * DateUtils.DAY_IN_MILLIS;
  }

  /**
   * 获取给定time时间 几天后的时间
   */
  public static Calendar getDateBefore(long time, int day) {
    Calendar now = Calendar.getInstance();
    now.setTimeInMillis(time);
    now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
    return now;
  }

  /**
   * 获取给定time时间 几天后的时间
   */
  public static Calendar getDateAfter(long time, int day) {
    Calendar now = Calendar.getInstance();
    now.setTimeInMillis(time);
    now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
    return now;
  }

  /**
   * 计算两个时间相差多少天,按天的维度，非24小时
   */
  public static int getDayInterval(long startTime, long endTime) {
    Calendar c1 = Calendar.getInstance();
    c1.setTime(new Date(startTime));
    Calendar c2 = Calendar.getInstance();
    c2.setTime(new Date(endTime));

    int days = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
    int y2 = c2.get(Calendar.YEAR);
    while (c1.get(Calendar.YEAR) != y2) {
      days += c1.getActualMaximum(Calendar.DAY_OF_YEAR);
      c1.add(Calendar.YEAR, 1);
    }
    return days;
  }

  /**
   * 将如 "2021-2-19" 这样的时间格式转化为时间戳
   * format 和 dateStr 格式一致
   */
  public static long getTime(SimpleDateFormat format, String dateStr) {
    long timestamp = 0;
    try {
      Date date = format.parse(dateStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      timestamp = cal.getTimeInMillis();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return timestamp;
  }

}
