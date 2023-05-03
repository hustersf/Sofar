package com.sofar.widget.recycler.adapter.multitype;

public class Type {

  public final Class mClass;
  public final CellFactory mFactory;
  public final Linker mLinker;

  public Type(Class clazz, CellFactory factory, Linker linker) {
    mClass = clazz;
    mFactory = factory;
    mLinker = linker;
  }
}
