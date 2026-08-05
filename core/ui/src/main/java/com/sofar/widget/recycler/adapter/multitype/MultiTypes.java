package com.sofar.widget.recycler.adapter.multitype;

import java.util.ArrayList;
import java.util.List;

public class MultiTypes {

  private List<Type> types = new ArrayList<>();

  public void register(Type type) {
    types.add(type);
  }

  public int firstIndex(Class clazz) {
    for (int i = 0; i < types.size(); i++) {
      Type type = types.get(i);
      if (clazz == type.mClass) {
        return i;
      }
    }
    return -1;
  }

  public Type getType(int index) {
    return types.get(index);
  }

}
