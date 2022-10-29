package com.sofar.widget.bar;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.R;

/**
 * 左边文字，右边箭头.样式无法涵盖所有 app
 * 实际业务场景中: 建议 copy 一份, 修改布局样式
 */
public class SofaSwitchBar extends FrameLayout {

  private TextView mLeftView;
  private TextView mLeftDescView;

  public SofaSwitchBar(@NonNull Context context) {
    this(context, null);
  }

  public SofaSwitchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SofaSwitchBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.bar_switch_item, this);
    initView();
  }

  private void initView() {
    mLeftView = findViewById(R.id.left_text);
    mLeftDescView = findViewById(R.id.left_text_desc);
  }

  public void setLeftText(CharSequence text) {
    mLeftView.setText(text);
  }

  public void setLeftDescText(CharSequence text) {
    mLeftDescView.setText(text);
    if (TextUtils.isEmpty(text)) {
      mLeftDescView.setVisibility(GONE);
    } else {
      mLeftDescView.setVisibility(VISIBLE);
    }
  }
}
