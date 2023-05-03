package com.sofar.widget.recycler;

import com.sofar.widget.recycler.adapter.expand.ExpandableAdapter;
import com.sofar.widget.recycler.adapter.expand.ExpandableCell;
import com.sofar.widget.recycler.cell.CityCell;
import com.sofar.widget.recycler.cell.CollegeCell;
import com.sofar.widget.recycler.cell.CollegeFamousCell;
import com.sofar.widget.recycler.cell.ProvinceCell;
import com.sofar.widget.recycler.model.College;
import com.sofar.widget.recycler.model.CollegeZone;

public class CollegeAdapter extends ExpandableAdapter<CollegeZone, College> {

  private static final int TYPE_PROVINCE = 1;
  private static final int TYPE_CITY = 2;

  private static final int TYPE_COLLEGE = -1;
  private static final int TYPE_FAMOUS = -2;

  @Override
  protected ExpandableCell onCreateGroupCell(int viewType) {
    if (viewType == TYPE_PROVINCE) {
      return new ProvinceCell();
    }
    return new CityCell();
  }

  @Override
  protected ExpandableCell onCreateChildCell(int viewType) {
    if (viewType == TYPE_FAMOUS) {
      return new CollegeFamousCell();
    }
    return new CollegeCell();
  }

  @Override
  public int getGroupItemViewType(int groupPosition) {
    if (getGroupItem(groupPosition).city) {
      return TYPE_CITY;
    }
    return TYPE_PROVINCE;
  }

  @Override
  public int getChildItemViewType(int groupPosition, int childPosition) {
    if (getChildItem(groupPosition, childPosition).famous) {
      return TYPE_FAMOUS;
    }
    return TYPE_COLLEGE;
  }

  @Override
  public int getChildCount(int groupPosition) {
    return getGroupItem(groupPosition).colleges.size();
  }

  @Override
  public College getChildItem(int groupPosition, int childPosition) {
    return getGroupItem(groupPosition).colleges.get(childPosition);
  }
}
