package com.sofar.im;

import com.sofar.im.link.WebSocketClient;
import com.sofar.im.msg.BaseMsg;

public class ImManager {

  String url = "ws://121.40.165.18:8800";
  WebSocketClient client = new WebSocketClient(url);

  String userId;

  private static class Inner {
    static ImManager instance = new ImManager();
  }

  private ImManager() {
  }

  public static ImManager get() {
    return Inner.instance;
  }


  public void connect(String userId) {
    this.userId = userId;
    client.setUserId(userId);
    client.connect();
  }

  public void sendMessage(BaseMsg msg) {
    //将msg对象转化成为字节数组
    client.sendMessage("测试数据".getBytes());
  }

  public void disconnect() {
    client.disconnect();
  }
}
