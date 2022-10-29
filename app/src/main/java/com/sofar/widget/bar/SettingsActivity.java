package com.sofar.widget.bar;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.keyboard.CommentInputDialog;
import com.sofar.utility.DateUtil;
import com.sofar.utility.ToastUtil;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    networkSwitchBar();
    rewardArrowBar();
  }

  private void networkSwitchBar() {
    SofaSwitchBar bar1 = findViewById(R.id.switch_bar1);
    bar1.setLeftText("使用3G/4G/5G网络播放");
    bar1.setLeftDescText("非wifi环境也会播放");

    SofaSwitchBar bar2 = findViewById(R.id.switch_bar2);
    bar2.setLeftText("使用3G/4G/5G网络下载");
  }

  private void rewardArrowBar() {
    SofaArrowBar bar1 = findViewById(R.id.arrow_bar1);
    bar1.setLeftText("现金金额");
    bar1.setRightHintText("点击选择金额");
    bar1.setOnClickListener(v -> {
      CommentInputDialog dialog = new CommentInputDialog();
      dialog.show(getSupportFragmentManager(), "comment_input");
    });

    SofaArrowBar bar2 = findViewById(R.id.arrow_bar2);
    bar2.setLeftText("红包个数");
    bar2.setRightHintText("点击选择红包个数");

    SofaArrowBar bar3 = findViewById(R.id.arrow_bar3);
    bar3.setLeftText("瓜分方式");
    bar3.setRightText("等额平分");
    bar3.hideRightIcon();


    SofaArrowBar bar4 = findViewById(R.id.arrow_bar4);
    bar4.setLeftText("开奖时间");
    bar4.setRightHintText("点击选择时间");

    bar4.setOnClickListener(v -> {
      ToastUtil.startShort(this, "日期选择器");
      bar4.setRightText(DateUtil.getTime(System.currentTimeMillis()));
    });
  }
}
