package com.sofar.utility.reflect;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成员变量
 * getDeclaredField是可以获取一个类本身的所有字段.
 * getField只能获取类及其父类的public字段.
 */
public class FieldUtil {

  private static final Map<String, Field> sFieldCache = new HashMap<>();

  private static String getKey(@NonNull Class<?> cls, String fieldName) {
    StringBuilder sb = new StringBuilder();
    sb.append(cls.toString()).append("#").append(fieldName);
    return sb.toString();
  }

  private static Field getField(Class<?> cls, String fieldName, final boolean forceAccess) {
    Validate.isTrue(cls != null, "The class must not be null");
    Validate.isTrue(!ReflectUtil.isEmpty(fieldName), "The field name must not be empty");

    String key = getKey(cls, fieldName);
    Field cacheField;
    synchronized (sFieldCache) {
      cacheField = sFieldCache.get(key);
    }

    // 直接从缓存中拿
    if (cacheField != null) {
      if (forceAccess && !cacheField.isAccessible()) {
        cacheField.setAccessible(true);
      }
      return cacheField;
    }

    // 父类中获取
    for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
      try {
        // getDeclaredField checks for non-public scopes as well
        Field field = acls.getDeclaredField(fieldName);
        if (!Modifier.isPublic(field.getModifiers())) {
          if (forceAccess) {
            field.setAccessible(true);
          } else {
            continue;
          }
          synchronized (sFieldCache) {
            sFieldCache.put(key, field);
          }
          return field;
        }
      } catch (NoSuchFieldException e) {
        // ignore
      }
    }

    // 检查所有实现的接口(接口中全是public修饰的)
    Field match = null;
    List<Class<?>> interfaces = ReflectUtil.getAllInterfaces(cls);
    if (interfaces != null) {
      for (Class<?> bcls : interfaces) {
        try {
          Field field = bcls.getField(fieldName);
          Validate.isTrue(match == null, "Reference to field %s is ambiguous relative to %s"
            + "; a matching field exists on two or more implemented interfaces.", fieldName, cls);
          match = field;
        } catch (NoSuchFieldException e) {
          // ignore
        }
      }
    }
    synchronized (sFieldCache) {
      sFieldCache.put(key, match);
    }
    return match;
  }

  /**
   * 获取指定cls中名字为fieldName的成员变量
   *
   * @param cls
   * @param fieldName
   * @return
   */
  public static Field getField(final String cls, final String fieldName) {
    try {
      return getField(Class.forName(cls), fieldName, true);
    } catch (Exception e) {
      ReflectUtil.print(e);
    }
    return null;
  }

  /**
   * 获取指定cls中名字为fieldName的成员变量
   *
   * @param cls
   * @param fieldName
   * @return
   */
  public static Field getField(final Class<?> cls, final String fieldName) {
    return getField(cls, fieldName, true);
  }

  /**
   * @param field
   * @param target
   * @param forceAccess
   * @return target对象的成员变量field的值
   * @throws IllegalAccessException
   */
  public static Object readField(final Field field, final Object target, final boolean forceAccess) throws IllegalAccessException {
    Validate.isTrue(field != null, "The field must not be null");
    if (forceAccess && !field.isAccessible()) {
      field.setAccessible(true);
    }
    return field.get(target);
  }

  /**
   * @param field
   * @param target
   * @return target对象的成员变量field的值
   * @throws IllegalAccessException
   */
  public static Object readField(final Field field, final Object target) throws IllegalAccessException {
    return readField(field, target, true);
  }

  /**
   * @param target
   * @param fieldName
   * @return target对象中名字为fieldName的成员变量的值
   */
  public static Object readField(final Object target, final String fieldName) throws IllegalAccessException {
    return readField(target, fieldName, true);
  }

  /**
   * @param target
   * @param fieldName
   * @param forceAccess
   * @return target对象中名字为fieldName的成员变量的值
   * @throws IllegalAccessException
   */
  public static Object readField(final Object target, final String fieldName, final boolean forceAccess) throws IllegalAccessException {
    Validate.isTrue(target != null, "target object must not be null");
    Class<?> cls = target.getClass();
    Field field = getField(cls, fieldName, forceAccess);
    Validate.isTrue(field != null, "cannot locate field %s on %s", fieldName, cls);
    return readField(field, target, forceAccess);
  }


  /**
   * 给target对象的成员变量field赋值为value
   *
   * @param field
   * @param target
   * @param value
   * @param forceAccess
   * @throws IllegalAccessException
   */
  public static void writeField(final Field field, final Object target, final Object value, final boolean forceAccess) throws IllegalAccessException {
    Validate.isTrue(field != null, "The field must not be null");
    if (forceAccess && !field.isAccessible()) {
      field.setAccessible(true);
    }
    field.set(target, value);
  }

  /**
   * 给target对象的成员变量field赋值为value
   *
   * @param field
   * @param target
   * @param value
   * @throws IllegalAccessException
   */
  public static void writeField(final Field field, final Object target, final Object value) throws IllegalAccessException {
    writeField(field, target, value, true);
  }

  /**
   * 给target对象的名字为fieldName的变量赋值为value
   *
   * @param target
   * @param fieldName
   * @param value
   */
  public static void writeField(final Object target, final String fieldName, final Object value) throws IllegalAccessException {
    writeField(target, fieldName, value, true);
  }

  /**
   * 给target对象的名字为fieldName的变量赋值为value
   *
   * @param target
   * @param fieldName
   * @param value
   * @param forceAccess
   */
  public static void writeField(final Object target, final String fieldName, final Object value, final boolean forceAccess) throws IllegalAccessException {
    Validate.isTrue(target != null, "target object must not be null");
    Class<?> cls = target.getClass();
    Field field = getField(cls, fieldName, forceAccess);
    Validate.isTrue(field != null, "cannot locate declared field %s.%s", cls.getName(), fieldName);
    writeField(field, target, value, forceAccess);
  }


  /**
   * 获取本类的名字为fieldName的成员变量
   *
   * @param cls
   * @param fieldName
   * @param forceAccess
   * @return
   */
  public static Field getDeclaredField(final String cls, final String fieldName, final boolean forceAccess) {
    try {
      return getDeclaredField(Class.forName(cls), fieldName, forceAccess);
    } catch (Throwable ignore) {
      ReflectUtil.print(ignore);
    }
    return null;
  }

  /**
   * 获取本类的名字为fieldName的成员变量
   *
   * @param cls
   * @param fieldName
   * @param forceAccess
   * @return
   */
  public static Field getDeclaredField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
    Validate.isTrue(cls != null, "The class must not be null");
    Validate.isTrue(!ReflectUtil.isEmpty(fieldName), "The field name must not be blank/empty");
    try {
      // only consider the specified class by using getDeclaredField()
      final Field field = cls.getDeclaredField(fieldName);
      if (!MemberUtil.isAccessible(field)) {
        if (forceAccess) {
          field.setAccessible(true);
        } else {
          return null;
        }
      }
      return field;
    } catch (final NoSuchFieldException e) { // NOPMD
      // ignore
    }
    return null;
  }

  /**
   * 给定target对象所在类本身的 名字为fieldName的字段 赋值为value
   *
   * @param target
   * @param fieldName
   * @param value
   * @throws IllegalAccessException
   */
  public static void writeDeclaredField(final Object target, final String fieldName, final Object value) throws IllegalAccessException {
    Validate.isTrue(target != null, "target object must not be null");
    final Class<?> cls = target.getClass();
    final Field field = getDeclaredField(cls, fieldName, true);
    Validate.isTrue(field != null, "cannot locate declared field %s.%s", cls.getName(), fieldName);
    // already forced access above, don't repeat it here:
    writeField(field, target, value, false);
  }

}
