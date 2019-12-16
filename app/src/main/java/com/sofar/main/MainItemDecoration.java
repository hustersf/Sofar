package com.sofar.main;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.utility.DeviceUtil;

public class MainItemDecoration extends RecyclerView.ItemDecoration {

  private int sideSpace;
  private int betweenSpace;

  public MainItemDecoration(Context context) {
    sideSpace = DeviceUtil.dp2px(context, 15);
    betweenSpace = DeviceUtil.dp2px(context, 10);
  }

  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    final int totalCount = parent.getAdapter().getItemCount();
    final int childPosition = parent.getChildAdapterPosition(view);

    outRect.top = childPosition == 0 ? sideSpace : betweenSpace;
    outRect.bottom = childPosition == totalCount - 1 ? sideSpace : 0;
    outRect.left = sideSpace;
    outRect.right = sideSpace;
  }
}
