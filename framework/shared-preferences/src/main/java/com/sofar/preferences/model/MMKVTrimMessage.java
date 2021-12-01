package com.sofar.preferences.model;

import java.io.Serializable;
import java.util.Map;

public class MMKVTrimMessage implements Serializable {
  public long beforeTrimKb;
  public long afterTrimKb;
  public String file;
  public String processName;
  public String stackTrace;
  public Map<String, Integer> valueSizeMap;
}
