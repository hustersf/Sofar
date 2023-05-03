package com.sofar.widget.recycler.model;

import androidx.annotation.Nullable;

public class College implements Comparable<College>{

  public String id;
  public int order;
  public String name;
  public String zone;
  @Nullable
  public String shortName;
  public boolean famous;

  @Override
  public int compareTo(College o) {
    return this.order - o.order;
  }
}
