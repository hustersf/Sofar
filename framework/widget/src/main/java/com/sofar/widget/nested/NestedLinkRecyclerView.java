package com.sofar.widget.nested;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NestedLinkRecyclerView extends RecyclerView implements NestedLinkScrollChild {

  public NestedLinkRecyclerView(@NonNull Context context) {
    super(context);
  }

  public NestedLinkRecyclerView(@NonNull Context context,
    @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public NestedLinkRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs,
    int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean fling(int velocityY) {
    return fling(0, velocityY);
  }

  @Override
  public void scrollToTop() {
    scrollToPosition(0);
  }

  @Override
  public void scrollToBottom() {
    scrollToPosition(getAdapter().getItemCount() - 1);
  }
}
