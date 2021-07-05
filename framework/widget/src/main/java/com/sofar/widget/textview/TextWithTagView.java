package com.sofar.widget.textview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sofar.widget.R;

/**
 * 文字后跟随任意个标签
 */
public class TextWithTagView extends LinearLayout {

  private static final String TAG = "TextWithTagView";

  private final int DEFAULT_TEXT_COLOR = 0xFF333333;
  private final int DEFAULT_TAG_COLOR = 0xFF999999;

  private int mTextColor;
  private String mText;
  private float mTextSize;
  private List<String> mTexts = new ArrayList<>();
  private float mTextSpace;

  private int mMaxLines;

  private int mTagColor;
  private float mTagTextSize;
  private List<String> mTags = new ArrayList<>();
  private float mTagSpace;
  private OnTagClickListener mTagClickListener;

  private TextPaint mTextPaint = new TextPaint();
  private TextPaint mTagPaint = new TextPaint();

  public TextWithTagView(Context context) {
    this(context, null);
  }

  public TextWithTagView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TextWithTagView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttr(attrs);
  }

  private void initAttr(AttributeSet attrs) {
    TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TextWithTagView);
    mText = ta.getString(R.styleable.TextWithTagView_tag_text);
    mTextColor = ta.getColor(R.styleable.TextWithTagView_tag_textColor, DEFAULT_TEXT_COLOR);
    mTextSize = ta.getDimension(R.styleable.TextWithTagView_tag_textSize, 16);
    mTagColor = ta.getColor(R.styleable.TextWithTagView_tag_tagColor, DEFAULT_TAG_COLOR);
    mTagTextSize = ta.getDimension(R.styleable.TextWithTagView_tag_tagSize, 16);
    mMaxLines = ta.getInt(R.styleable.TextWithTagView_tag_maxLines, 2);
    mTextSpace = ta.getDimension(R.styleable.TextWithTagView_tag_textSpace, 0);
    mTagSpace = ta.getDimension(R.styleable.TextWithTagView_tag_space, 0);
    ta.recycle();

    mTextPaint.setColor(mTextColor);
    mTextPaint.setTextSize(mTextSize);

    mTagPaint.setColor(mTagColor);
    mTagPaint.setTextSize(mTagTextSize);

    setOrientation(LinearLayout.VERTICAL);
  }

  public void setTags(@NonNull List<String> tags) {
    mTags.clear();
    mTags.addAll(tags);
  }

  public void setText(String text) {
    mText = text;
  }

  /**
   * 调用此方法刷新内容
   */
  public void update() {
    if (mText == null) {
      return;
    }

    if (getWidth() == 0) {
      post(this::updateInner);
    } else {
      updateInner();
    }
  }

  private void updateInner() {
    mTexts.clear();
    DynamicLayout layout =
      new DynamicLayout(mText, mTextPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, 0, 0, false);
    int lineCount = layout.getLineCount();
    int start = 0;
    int end;
    for (int i = 0; i < lineCount; i++) {
      end = layout.getLineEnd(i);
      String line = mText.substring(start, end); //指定行的内容
      start = end;
      mTexts.add(line);
      Log.d(TAG, " lineText== " + line);
    }

    removeAllViews();
    addTextNoTag();
    addTextWithTag();
  }

  private void addTextNoTag() {
    for (int i = 0; i < mTexts.size(); i++) {
      if (i == mMaxLines - 1) {
        break;
      }

      ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
      TextView textView = new TextView(getContext());
      textView.setMaxLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setTextColor(mTextColor);
      textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
      textView.setText(mTexts.get(i));
      addView(textView, lp);
    }
  }

  private void addTextWithTag() {
    LinearLayout layout = new LinearLayout(getContext());
    layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT));
    layout.setOrientation(LinearLayout.HORIZONTAL);
    layout.setGravity(Gravity.CENTER_VERTICAL);
    addView(layout);

    int maxWidth = getWidth();
    float tagWidth = 0;
    int tagShowCount = 0;
    for (int i = 0; i < mTags.size(); i++) {
      String tag = mTags.get(i);
      tagWidth += mTagPaint.measureText(tag);
      if (tagWidth <= maxWidth) {
        tagShowCount++;
      }
    }

    float extraSpace = mTextSpace;
    if (tagShowCount > 0) {
      extraSpace += (tagShowCount - 1) * mTagSpace;
    }

    boolean tagWithText = false;
    if (mMaxLines <= mTexts.size() && mMaxLines >= 1) {
      String text = mTexts.get(mMaxLines - 1);
      int lastLineTextWidth =
        (int) Math.min(maxWidth - tagWidth - extraSpace, mTextPaint.measureText(text));
      ViewGroup.LayoutParams lp =
        new ViewGroup.LayoutParams(lastLineTextWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
      TextView textView = new TextView(getContext());
      textView.setMaxLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setTextColor(mTextColor);
      textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
      textView.setText(text);
      layout.addView(textView, lp);
      tagWithText = true;
    }

    for (int i = 0; i < mTags.size(); i++) {
      if (i == tagShowCount) {
        break;
      }
      LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT);
      if (i == 0) {
        lp.leftMargin = tagWithText ? (int) mTextSpace : 0;
      } else {
        lp.leftMargin = (int) mTagSpace;
      }

      final int position = i;
      TextView textView = new TextView(getContext());
      textView.setMaxLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setTextColor(mTagColor);
      textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTagTextSize);
      textView.setText(mTags.get(i));
      textView.setOnClickListener(v -> {
        if (mTagClickListener != null) {
          mTagClickListener.onTagClick(position, textView);
        }
      });
      layout.addView(textView, lp);
    }
  }

  public void setOnTagClickListener(OnTagClickListener listener) {
    this.mTagClickListener = listener;
  }

  public interface OnTagClickListener {
    void onTagClick(int position, View view);
  }

}
