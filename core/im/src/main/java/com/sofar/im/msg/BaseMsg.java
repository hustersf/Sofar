package com.sofar.im.msg;

import com.sofar.im.link.Util;

/**
 * 消息
 */
public class BaseMsg {


  String id;
  String targetId;
  @TargetType
  int targetType;
  @MsgType
  int msgType;
  byte[] contentBytes;

  public BaseMsg(@TargetType int targetType, String targetId) {
    id = Util.randomId();
    this.targetType = targetType;
    this.targetId = targetId;
  }

  public void setMsgType(@MsgType int msgType) {
    this.msgType = msgType;
  }

  public void setContentBytes(byte[] contentBytes) {
    this.contentBytes = contentBytes;
  }

}
