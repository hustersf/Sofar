package com.sofar.im.link;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {

  private static final String TAG = "WebSocketClient";

  private static OkHttpClient sClient = new OkHttpClient();
  String url;
  String userId;

  @NonNull
  private WebSocket webSocket;

  public WebSocketClient(String url) {
    this.url = url;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void connect() {
    Request request = new Request.Builder()
      .header("userId", userId)
      .url(url)
      .build();
    webSocket = sClient.newWebSocket(request, listener);
  }

  public void disconnect() {
    webSocket.close(1000, "app use disconnect function");
  }


  public void sendMessage(byte[] bytes) {
    webSocket.send(ByteString.of(bytes));
  }

  WebSocketListener listener = new WebSocketListener() {
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
      super.onOpen(webSocket, response);
      Log.d(TAG, "open:" + response.toString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
      super.onMessage(webSocket, text);
      Log.d(TAG, "onMessage:" + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
      super.onMessage(webSocket, bytes);
      Log.d(TAG, "onMessage:" + bytes.toString());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
      super.onClosing(webSocket, code, reason);
      Log.d(TAG, "onClosing:{code=" + code + " reason=" + reason + "}");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
      super.onClosed(webSocket, code, reason);
      Log.d(TAG, "closed:{code=" + code + " reason=" + reason + "}");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
      super.onFailure(webSocket, t, response);
    }
  };
}
