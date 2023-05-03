package com.sofar.widget.recycler.cell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.widget.recycler.adapter.expand.ExpandableCell;
import com.sofar.widget.recycler.model.CollegeZone;

public class ProvinceCell extends ExpandableCell<CollegeZone, CollegeZone> {

  private TextView titleTv;
  private ImageView arrowIv;

  @Override
  protected View createView(@NonNull ViewGroup parent) {
    return LayoutInflater.from(parent.getContext())
      .inflate(R.layout.college_province_item, parent, false);
  }

  @Override
  protected void onCreate(@NonNull View rootView) {
    super.onCreate(rootView);
    titleTv = rootView.findViewById(R.id.title);
    arrowIv = rootView.findViewById(R.id.arrow_image);
  }

  @Override
  protected void onBindGroup(@NonNull CollegeZone collegeZone, boolean expand) {
    super.onBindGroup(collegeZone, expand);
    titleTv.setText(collegeZone.name);
    arrowIv.setRotation(expand ? 0 : -90);
  }

}
