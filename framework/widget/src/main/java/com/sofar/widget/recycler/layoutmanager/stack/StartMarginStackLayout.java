package com.sofar.widget.recycler.layoutmanager.stack;

public class StartMarginStackLayout extends DefaultLayout {

  int startMargin;

  public StartMarginStackLayout(int orientation, int visibleCount, int itemOffset) {
    super(orientation, visibleCount, itemOffset);
  }

  @Override
  protected int getStartMargin() {
    return startMargin;
  }

  public StartMarginStackLayout setStartMargin(int startMargin) {
    this.startMargin = startMargin;
    return this;
  }
}
