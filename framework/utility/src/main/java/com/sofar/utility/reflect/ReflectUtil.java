package com.sofar.utility.reflect;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

class ReflectUtil {

  static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

  static boolean isSameLength(final Object[] array1, final Object[] array2) {
    if ((array1 == null && array2 != null && array2.length > 0) ||
      (array2 == null && array1 != null && array1.length > 0) ||
      (array1 != null && array2 != null && array1.length != array2.length)) {
      return false;
    }
    return true;
  }

  static Class<?>[] toClass(final Object... array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_CLASS_ARRAY;
    }

    Class<?>[] classes = new Class[array.length];
    for (int i = 0; i < array.length; i++) {
      classes[i] = array[i] == null ? null : array[i].getClass();
    }
    return classes;
  }

  static Class<?>[] nullToEmpty(final Class<?>[] array) {
    if (array == null || array.length == 0) {
      return EMPTY_CLASS_ARRAY;
    }
    return array;
  }

  static Object[] nullToEmpty(final Object[] array) {
    if (array == null || array.length == 0) {
      return EMPTY_OBJECT_ARRAY;
    }
    return array;
  }


  /**
   * 获取指定class 实现的全部接口
   */
  public static List<Class<?>> getAllInterfaces(Class<?> cls) {
    if (cls == null) {
      return null;
    }
    final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
    getAllInterfaces(cls, interfacesFound);
    return new ArrayList<>(interfacesFound);
  }

  private static void getAllInterfaces(Class<?> cls, @NonNull HashSet<Class<?>> interfacesFound) {
    while (cls != null) {

      //获取该类的直接实现接口
      Class<?>[] interfaces = cls.getInterfaces();
      for (Class<?> i : interfaces) {
        if (interfacesFound.add(i)) {
          //接口实现的接口
          getAllInterfaces(i, interfacesFound);
        }
      }

      //父类的接口
      cls = cls.getSuperclass();
    }
  }

  static boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }

  static void print(Throwable throwable) {
    if (throwable != null) {
      throwable.printStackTrace();
    }
  }

}
