package com.sofar.network.response;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sofar.util.JsonUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 对服务端的数据统一处理，只关心data中的数据
 */
public class ResponseDeserializer implements JsonDeserializer<Response> {

  private static final String KEY_CODE = "errorCode";
  private static final String KEY_MESSAGE = "errorMsg";
  private static final String KEY_DATA = "data";

  @Override
  public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
    final JsonObject obj = (JsonObject) json;
    final Type responseType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
    int errorCode = JsonUtil.optInt(obj, KEY_CODE, 0);
    String errorMessage = JsonUtil.optString(obj, KEY_MESSAGE, null);
    JsonElement data = JsonUtil.optElement(obj, KEY_DATA);
    Object body = responseType == String.class ? data.toString() : context.deserialize(data, responseType);
    return new Response<>(body, errorCode, errorMessage);
  }
}
