package com.sofar.widget.recycler.model;

import java.util.List;

public class CollegeWrapper {

  public final List<CollegeZone> zone;
  public final List<College> university;

  public CollegeWrapper(List<CollegeZone> zone, List<College> university) {
    this.zone = zone;
    this.university = university;
  }
}
