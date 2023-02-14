package com.sofar.utility.calendar;

/**
 * 定义日历事件参数
 * 其中 count>0 表示重复事件
 */
public class CalendarInfo {

  public String title;
  public String description;

  // 日历开始时间
  public long startTime;
  //日历结束时间(非重复日历使用)
  public long endTime;
  //日历提醒持续多少秒, 比如3600代表一小时,重复日历使用
  public long durationSeconds;
  //日历提前几分钟开始提醒
  public int remindMinutes;
  //日历重复规则,按天/周/月
  @RepeatRule
  public int repeat;
  //日历事件重复多少次(count>1),count=1 表示单次事件
  public int count;

  public @interface RepeatRule {
    int DAY = 0;
    int WEEK = 1;
    int MONTH = 2;
  }
}
