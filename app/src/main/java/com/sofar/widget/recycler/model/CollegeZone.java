package com.sofar.widget.recycler.model;

import java.util.ArrayList;
import java.util.List;

public class CollegeZone implements Comparable<CollegeZone> {

  public String id;
  public int sort;
  public String name;
  public boolean city;
  public transient final List<College> colleges = new ArrayList<>();

  @Override
  public int compareTo(CollegeZone o) {
    return this.sort - o.sort;
  }
}
