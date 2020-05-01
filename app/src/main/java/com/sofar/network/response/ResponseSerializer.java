package com.sofar.network.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 *  如果尝试序列化 Response 直接崩溃.
 */
public class ResponseSerializer implements JsonSerializer {

  @Override
  public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
    throw new RuntimeException("Response can't to json");
  }
}
