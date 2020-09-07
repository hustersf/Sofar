package com.sofar.base.exception;

import com.sofar.base.BuildConfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;

public class SofarErrorConsumer implements Consumer<Throwable> {

  static List<Class<? extends Exception>> list = new ArrayList<>();

  /**
   * 可在此更新 想抛出的异常类型
   */
  static {
    list.add(NullPointerException.class);
    list.add(IndexOutOfBoundsException.class);
    list.add(IllegalStateException.class);
    list.add(ArithmeticException.class);
    list.add(ClassCastException.class);
    list.add(SQLException.class);
  }

  @Override
  public void accept(Throwable t) throws Exception {
    if (BuildConfig.DEBUG && t instanceof Exception) {
      Exception e = (Exception) t;
      if (t instanceof OnErrorNotImplementedException || t instanceof UndeliverableException
        && t.getCause() instanceof Exception) {
        e = (Exception) t.getCause();
      }
      for (Class<? extends Exception> cls : list) {
        if (cls.isAssignableFrom(e.getClass())) {
          throw e;
        }
      }
    }
  }
}
