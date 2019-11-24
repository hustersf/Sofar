package com.sofar.utility.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Member,类的成员
 * Modifier,访问修饰符
 */
class MemberUtil {

  private static final int ACCESS_TEST = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
  private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = {Byte.TYPE, Short.TYPE,
    Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};

  private static boolean isPackageAccess(int modifiers) {
    return (modifiers & ACCESS_TEST) == 0;
  }

  static boolean isAccessible(Member m) {
    return m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic();
  }

  static boolean setAccessibleWorkaround(final AccessibleObject o) {
    if (o == null || o.isAccessible()) {
      return false;
    }

    if (o instanceof Member) {
      Member m = (Member) o;
      if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
        try {
          o.setAccessible(true);
          return true;
        } catch (SecurityException e) {
          // ignore in favor of subsequent IllegalAccessException
        }
      }
    }

    return false;
  }

  static boolean isAssignable(final Class<?> cls, final Class<?> toClass) {
    return isAssignable(cls, toClass, true);
  }

  static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, final boolean autobox) {
    if (!ReflectUtil.isSameLength(classArray, toClassArray)) {
      return false;
    }

    if (classArray == null) {
      classArray = ReflectUtil.EMPTY_CLASS_ARRAY;
    }

    if (toClassArray == null) {
      toClassArray = ReflectUtil.EMPTY_CLASS_ARRAY;
    }

    for (int i = 0; i < classArray.length; i++) {
      if (!isAssignable(classArray[i], toClassArray[i], autobox)) {
        return false;
      }
    }

    return true;
  }

  /**
   * toClass 是否是cls的父类或本身
   */
  static boolean isAssignable(Class<?> cls, final Class<?> toClass, final boolean autobox) {
    if (toClass == null) {
      return false;
    }
    // have to check for null, as isAssignableFrom doesn't
    if (cls == null) {
      return !toClass.isPrimitive();
    }

    // 自动装箱
    if (autobox) {
      if (cls.isPrimitive() && !toClass.isPrimitive()) {
        cls = primitiveToWrapper(cls);
        if (cls == null) {
          return false;
        }
      }

      if (!cls.isPrimitive() && toClass.isPrimitive()) {
        cls = wrapperToPrimitive(cls);
        if (cls == null) {
          return false;
        }
      }
    }

    if (cls.equals(toClass)) {
      return true;
    }

    if (cls.isPrimitive()) {
      if (!toClass.isPrimitive()) {
        return false;
      }

      if (Boolean.TYPE.equals(cls)) {
        return false;
      }

      if (Character.TYPE.equals(cls)) {
        return Integer.TYPE.equals(toClass)
          || Long.TYPE.equals(toClass)
          || Float.TYPE.equals(toClass)
          || Double.TYPE.equals(toClass);
      }

      if (Byte.TYPE.equals(cls)) {
        return Short.TYPE.equals(toClass)
          || Integer.TYPE.equals(toClass)
          || Long.TYPE.equals(toClass)
          || Float.TYPE.equals(toClass)
          || Double.TYPE.equals(toClass);
      }

      if (Short.TYPE.equals(cls)) {
        return Integer.TYPE.equals(toClass)
          || Long.TYPE.equals(toClass)
          || Float.TYPE.equals(toClass)
          || Double.TYPE.equals(toClass);
      }

      if (Integer.TYPE.equals(cls)) {
        return Long.TYPE.equals(toClass)
          || Float.TYPE.equals(toClass)
          || Double.TYPE.equals(toClass);
      }

      if (Long.TYPE.equals(cls)) {
        return Float.TYPE.equals(toClass)
          || Double.TYPE.equals(toClass);
      }

      if (Float.TYPE.equals(cls)) {
        return Double.TYPE.equals(toClass);
      }

      if (Double.TYPE.equals(cls)) {
        return false;
      }

      //Void
      return false;
    }

    return toClass.isAssignableFrom(cls);


  }


  private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

  static {
    primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
    primitiveWrapperMap.put(Character.TYPE, Character.class);
    primitiveWrapperMap.put(Byte.TYPE, Byte.class);
    primitiveWrapperMap.put(Short.TYPE, Short.class);
    primitiveWrapperMap.put(Integer.TYPE, Integer.class);
    primitiveWrapperMap.put(Long.TYPE, Long.class);
    primitiveWrapperMap.put(Float.TYPE, Float.class);
    primitiveWrapperMap.put(Double.TYPE, Double.class);
    primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
  }

  private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();

  static {
    for (Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
      Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
      if (!primitiveClass.equals(wrapperClass)) {
        wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
      }
    }
  }

  static Class<?> primitiveToWrapper(final Class<?> cls) {
    Class<?> convertedClass = cls;
    if (cls != null && cls.isPrimitive()) {
      convertedClass = primitiveWrapperMap.get(cls);
    }

    return convertedClass;
  }

  static Class<?> wrapperToPrimitive(final Class<?> cls) {
    return wrapperPrimitiveMap.get(cls);
  }


  static int compareParameterTypes(final Class<?>[] left, final Class<?>[] right, final Class<?>[] actual) {
    final float leftCost = getTotalTransformationCost(actual, left);
    final float rightCost = getTotalTransformationCost(actual, right);
    return leftCost < rightCost ? -1 : rightCost < leftCost ? 1 : 0;
  }

  private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Class<?>[] destArgs) {
    float totalCost = 0.0f;
    for (int i = 0; i < srcArgs.length; i++) {
      Class<?> srcClass, destClass;
      srcClass = srcArgs[i];
      destClass = destArgs[i];
      totalCost += getObjectTransformationCost(srcClass, destClass);
    }
    return totalCost;
  }

  private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> destClass) {
    if (destClass.isPrimitive()) {
      return getPrimitivePromotionCost(srcClass, destClass);
    }
    float cost = 0.0f;
    while (srcClass != null && !destClass.equals(srcClass)) {
      if (destClass.isInterface() && isAssignable(srcClass, destClass)) {
        // slight penalty for interface match.
        // we still want an exact match to override an interface match,
        // but
        // an interface match should override anything where we have to
        // get a superclass.
        cost += 0.25f;
        break;
      }
      cost++;
      srcClass = srcClass.getSuperclass();
    }
    /*
     * If the destination class is null, we've travelled all the way up to
     * an Object match. We'll penalize this by adding 1.5 to the cost.
     */
    if (srcClass == null) {
      cost += 1.5f;
    }
    return cost;
  }

  private static float getPrimitivePromotionCost(final Class<?> srcClass, final Class<?> destClass) {
    float cost = 0.0f;
    Class<?> cls = srcClass;
    if (!cls.isPrimitive()) {
      // slight unwrapping penalty
      cost += 0.1f;
      cls = wrapperToPrimitive(cls);
    }
    for (int i = 0; cls != destClass && i < ORDERED_PRIMITIVE_TYPES.length; i++) {
      if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
        cost += 0.1f;
        if (i < ORDERED_PRIMITIVE_TYPES.length - 1) {
          cls = ORDERED_PRIMITIVE_TYPES[i + 1];
        }
      }
    }
    return cost;
  }

}
