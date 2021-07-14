package com.sofar.widget.textview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.sofar.widget.textview.span.CenterImageSpan;
import com.sofar.widget.textview.span.ProtocolSpan;

/**
 * 文字后跟随任意个标签
 */
public class TextTagView extends AppCompatTextView {

  private static final String TAG = "TextTagView";
  private static final String SUFFIX = "...";

  private String mText;
  private List<String> mTexts = new ArrayList<>();
  private float mTextSpace;

  private List<Integer> mTagColors = new ArrayList<>();
  private List<String> mTags = new ArrayList<>();
  private float mTagSpace;
  private OnTagClickListener mTagClickListener;
  private boolean mTagBold;

  public TextTagView(Context context) {
    this(context, null);
  }

  public TextTagView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TextTagView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void clearTags() {
    mTags.clear();
    mTagColors.clear();
  }

  public void addTag(String tag, int tagColor) {
    mTags.add(tag);
    mTagColors.add(tagColor);
  }

  public void setContent(String text) {
    mText = text;
    setText(text);
  }

  public void setTagBold(boolean bold) {
    mTagBold = bold;
  }

  public void setTextSpace(float textSpace) {
    mTextSpace = textSpace;
  }

  public void setTagSpace(float tagSpace) {
    mTagSpace = tagSpace;
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
    mTexts.addAll(getTextLines(mText, getPaint(), getWidth()));
    calculate();
  }

  private void calculate() {
    int maxWidth = getWidth();
    Log.d(TAG, "maxWidth=" + maxWidth);
    float tagWidth = 0;
    int tagShowCount = 0;
    for (int i = 0; i < mTags.size(); i++) {
      String tag = mTags.get(i);
      if (tagWidth + getPaint().measureText(tag) <= maxWidth - extraSpace(mTags.size())) {
        tagShowCount++;
        tagWidth += getPaint().measureText(tag);
      } else {
        break;
      }
    }

    int maxLine = getMaxLines();
    if (maxLine < 1) {
      maxLine = 1;
    }

    String tagText = "";  //和tag在一行的文字
    StringBuffer titleSb = new StringBuffer();
    for (int i = 0; i < mTexts.size(); i++) {
      if (tagShowCount > 0 && i == maxLine - 1) {
        tagText = mTexts.get(i);
        break;
      }
      titleSb.append(mTexts.get(i));
    }

    boolean hasSuffix = false;
    float overWidth = maxWidth - tagWidth - extraSpace(tagShowCount);
    float tagTextWidth = getPaint().measureText(tagText);
    if (tagTextWidth > overWidth) {
      List<String> lines = getTextLines(tagText, getPaint(), (int) overWidth);
      if (lines.size() > 0) {
        titleSb.append(lines.get(0));
      }
      hasSuffix = true;
    } else {
      titleSb.append(tagText);
    }

    String title = titleSb.toString();
    if (hasSuffix && title.length() > 2) {
      title = title.substring(0, title.length() - 2) + SUFFIX;
    }

    boolean tagSingleLine = false;
    if (maxLine > mTexts.size() || getPaint().measureText(title) < getWidth()) {
      title += "\n";
      tagSingleLine = true;
    }

    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    showText(title, spannableStringBuilder);
    if (!tagSingleLine) {
      setSpanPadding(spannableStringBuilder, (int) mTextSpace);
    }
    if (tagShowCount > 0) {
      showTag(mTags.subList(0, tagShowCount), spannableStringBuilder);
    }
    setText(spannableStringBuilder);
  }

  private float extraSpace(int tagShowCount) {
    if (tagShowCount > 0) {
      return mTextSpace + (tagShowCount - 1) * mTagSpace;
    }
    return 0;
  }

  private void showText(String title, SpannableStringBuilder spannableString) {
    Log.d(TAG, "showText=" + title);
    spannableString.append(title);
  }

  private void showTag(@NonNull List<String> subTag, SpannableStringBuilder spannableString) {
    Log.d(TAG, "showTag=" + subTag.size());
    for (int i = 0; i < subTag.size(); i++) {
      String tag = subTag.get(i);
      spannableString.append(tag);
      int tagStart = spannableString.length() - tag.length();
      int tagEnd = spannableString.length();
      ProtocolSpan span = new ProtocolSpan();
      span.setFakeBoldText(mTagBold);
      span.setLinkColor(mTagColors.get(i));
      final int position = i;
      span.setOnProtocolClickListener(() -> {
        if (mTagClickListener != null) {
          mTagClickListener.onTagClick(position);
        }
      });
      spannableString.setSpan(span, tagStart, tagEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
      if (i != subTag.size() - 1) {
        setSpanPadding(spannableString, (int) mTagSpace);
      }
    }
  }

  private void setSpanPadding(SpannableStringBuilder spannableString, int padding) {
    spannableString.append(" ");
    Drawable placeDrawable = new ColorDrawable();
    CenterImageSpan span = new CenterImageSpan(placeDrawable);
    placeDrawable.setBounds(0, 0, padding, 0);
    spannableString.setSpan(span, spannableString.length() - 1, spannableString.length(),
      Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
  }

  @NonNull
  private List<String> getTextLines(String text, TextPaint textPaint, int width) {
    if (TextUtils.isEmpty(text) || width <= 0) {
      return new ArrayList<>();
    }

    Log.d(TAG, "start text line part");
    List<String> list = new ArrayList<>();
    DynamicLayout layout =
      new DynamicLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 0, 0, false);
    int lineCount = layout.getLineCount();
    int start = 0;
    int end;
    for (int i = 0; i < lineCount; i++) {
      end = layout.getLineEnd(i);
      String line = text.substring(start, end); //指定行的内容
      start = end;
      list.add(line);
      Log.d(TAG, " lineText== " + line);
    }
    return list;
  }

  public void setOnTagClickListener(OnTagClickListener listener) {
    this.mTagClickListener = listener;
  }

  public interface OnTagClickListener {
    void onTagClick(int position);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    TextView widget = this;
    CharSequence text = getText();
    if (text instanceof SpannedString) {
      if (action == MotionEvent.ACTION_UP ||
        action == MotionEvent.ACTION_DOWN) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        ClickableSpan[] link = ((SpannedString) text).getSpans(off, off, ClickableSpan.class);

        if (link.length != 0) {
          if (action == MotionEvent.ACTION_UP) {
            link[0].onClick(widget);
          }
          return true;
        }
      }
    }
    return super.onTouchEvent(event);
  }


  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    Log.d(TAG, "onSizeChanged w=" + w + " h=" + h + " oldw=" + oldw + " oldh=" + oldh);
    if (oldw != 0 && oldh != 0) {
      update();
    }
  }
}
