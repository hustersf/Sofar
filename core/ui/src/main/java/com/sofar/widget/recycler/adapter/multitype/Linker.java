package com.sofar.widget.recycler.adapter.multitype;

public interface Linker<T> {

  int index(int position, T data);
}
