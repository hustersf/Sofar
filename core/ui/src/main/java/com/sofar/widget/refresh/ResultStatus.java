package com.sofar.widget.refresh;

public interface ResultStatus {

  boolean isAvailable();

  void onPrepare();

  void onShow();

  void onHide();
}
