package com.sofar.im.msg;

public class TextMsg extends BaseMsg {

  public TextMsg(@TargetType int targetType, String targetId, String text) {
    super(targetType, targetId);
    setMsgType(MsgType.TEXT);
    setContentBytes(text.getBytes());
  }

}
