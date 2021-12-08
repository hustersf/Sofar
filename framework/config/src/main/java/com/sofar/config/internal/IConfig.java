package com.sofar.config.internal;

public interface IConfig {

  default void setInt(String key, int value) {
    setValue(key, int.class, value);
  }

  default int getInt(String key, int defValue) {
    return getValue(key, int.class, defValue);
  }

  default void setLong(String key, long value) {
    setValue(key, long.class, value);
  }

  default long getLong(String key, long defValue) {
    return getValue(key, long.class, defValue);
  }

  default void setFloat(String key, float value) {
    setValue(key, float.class, value);
  }

  default float getFloat(String key, float defValue) {
    return getValue(key, float.class, defValue);
  }

  default void setBoolean(String key, boolean value) {
    setValue(key, boolean.class, value);
  }

  default boolean getBoolean(String key, boolean defValue) {
    return getValue(key, boolean.class, defValue);
  }

  default void setString(String key, String value) {
    setValue(key, String.class, value);
  }

  default String getString(String key, String defValue) {
    return getValue(key, String.class, defValue);
  }

  <T> T getValue(String key, Class<T> classOfT, T defaultValue);

  <T> void setValue(String key, Class<T> classOfT, T value);

}
