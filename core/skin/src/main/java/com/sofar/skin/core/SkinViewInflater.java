package com.sofar.skin.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;

import androidx.collection.ArrayMap;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * {@link androidx.appcompat.app.AppCompatViewInflater}
 */
public class SkinViewInflater {

  private static final String[] sClassPrefixList = {
    "android.widget.",
    "android.view.",
    "android.webkit."
  };

  private static final Class<?>[] sConstructorSignature = new Class<?>[]{Context.class, AttributeSet.class};
  private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();
  private final static Object[] mConstructorArgs = new Object[2];

  static View createViewFromTag(Context context, String name, AttributeSet attrs) {
    if (name.equals("view")) {
      name = attrs.getAttributeValue(null, "class");
    }

    try {
      mConstructorArgs[0] = context;
      mConstructorArgs[1] = attrs;

      if (-1 == name.indexOf('.')) {
        for (int i = 0; i < sClassPrefixList.length; i++) {
          final View view = createViewByPrefix(context, name, sClassPrefixList[i]);
          if (view != null) {
            return view;
          }
        }
        return null;
      } else {
        return createViewByPrefix(context, name, null);
      }
    } catch (Exception e) {
      // We do not want to catch these, lets return null and let the actual LayoutInflater
      // try
      return null;
    } finally {
      // Don't retain references on context.
      mConstructorArgs[0] = null;
      mConstructorArgs[1] = null;
    }
  }

  private static View createViewByPrefix(Context context, String name, String prefix)
    throws ClassNotFoundException, InflateException {
    Constructor<? extends View> constructor = sConstructorMap.get(name);

    try {
      if (constructor == null) {
        // Class not found in the cache, see if it's real, and try to add it
        Class<? extends View> clazz = Class.forName(
          prefix != null ? (prefix + name) : name,
          false,
          context.getClassLoader()).asSubclass(View.class);

        constructor = clazz.getConstructor(sConstructorSignature);
        sConstructorMap.put(name, constructor);
      }
      constructor.setAccessible(true);
      return constructor.newInstance(mConstructorArgs);
    } catch (Exception e) {
      // We do not want to catch these, lets return null and let the actual LayoutInflater
      // try
      return null;
    }
  }

}
