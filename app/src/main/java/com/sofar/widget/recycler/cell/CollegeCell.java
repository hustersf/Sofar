package com.sofar.widget.recycler.cell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.widget.recycler.adapter.expand.ExpandableCell;
import com.sofar.widget.recycler.model.College;
import com.sofar.widget.recycler.model.CollegeZone;

public class CollegeCell extends ExpandableCell<CollegeZone, College> {

  private TextView titleTv;

  @Override
  protected View createView(@NonNull ViewGroup parent) {
    return LayoutInflater.from(parent.getContext())
      .inflate(R.layout.college_item, parent, false);
  }

  @Override
  protected void onCreate(@NonNull View rootView) {
    super.onCreate(rootView);
    titleTv = rootView.findViewById(R.id.title);
  }

  @Override
  protected void onBindChild(@NonNull College college, boolean expand) {
    super.onBindChild(college, expand);
    titleTv.setText(college.name);
  }
}
