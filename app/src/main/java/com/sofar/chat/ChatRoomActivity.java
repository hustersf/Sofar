package com.sofar.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.im.ImConfig;
import com.sofar.im.ImManager;
import com.sofar.im.ImSdk;
import com.sofar.im.msg.TargetType;
import com.sofar.im.msg.TextMsg;

public class ChatRoomActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("聊天室");

    //请放在application中初始化和连接
    ImSdk.init(this, new ImConfig.Builder()
      .appId("2020")
      .appName("sofar")
      .build());
    ImManager.get().connect("123");

    TextMsg textMsg = new TextMsg(TargetType.SINGLE, "456", "测试文本消息");
    ImManager.get().sendMessage(textMsg);
  }
}
