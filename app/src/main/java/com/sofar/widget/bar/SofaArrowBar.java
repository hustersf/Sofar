package com.sofar.widget.bar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.R;

/**
 * 左边文字，右边箭头.样式无法涵盖所有 app
 * 实际业务场景中: 建议 copy 一份, 修改布局样式
 */
public class SofaArrowBar extends FrameLayout {

  private TextView mLeftTextView;
  private TextView mRightTextView;
  private ImageView mRightIconView;

  public SofaArrowBar(@NonNull Context context) {
    this(context, null);
  }

  public SofaArrowBar(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SofaArrowBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.bar_arrow_item, this);
    initView();
  }

  private void initView() {
    mLeftTextView = findViewById(R.id.left_text);
    mRightTextView = findViewById(R.id.right_text);
    mRightIconView = findViewById(R.id.right_icon);
  }

  public void setLeftText(CharSequence text) {
    mLeftTextView.setText(text);
  }

  public void setRightText(CharSequence text) {
    mRightTextView.setText(text);
  }

  public void setRightHintText(CharSequence text) {
    mRightTextView.setHint(text);
  }

  public void hideRightText() {
    mRightTextView.setVisibility(GONE);
  }

  public void hideRightIcon() {
    mRightIconView.setVisibility(GONE);
  }

}
