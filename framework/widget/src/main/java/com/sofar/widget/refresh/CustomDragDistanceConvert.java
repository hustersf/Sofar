package com.sofar.widget.refresh;

public class CustomDragDistanceConvert implements DragDistanceConverter {
  private int MAX_SCROLL_DISTANCE;

  public CustomDragDistanceConvert(int maxScrollDistance) {
    MAX_SCROLL_DISTANCE = maxScrollDistance;
  }

  @Override
  public float convert(float scrollDistance, float refreshDistance) {
    return MAX_SCROLL_DISTANCE - 2 * (MAX_SCROLL_DISTANCE * MAX_SCROLL_DISTANCE)
      / (scrollDistance + 2 *
      MAX_SCROLL_DISTANCE); // (MAX * distance) / (distance + 2 * MAX), result range from 0 to
    // MAX / 3
  }

  @Override
  public float reverseConvert(float moveDistance, float refreshDistance) {
    return 2 * MAX_SCROLL_DISTANCE * MAX_SCROLL_DISTANCE / (MAX_SCROLL_DISTANCE - moveDistance) -
      2 * MAX_SCROLL_DISTANCE;
  }
}
