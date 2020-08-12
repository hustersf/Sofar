package com.sofar.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;
import com.sofar.base.span.SpanUtil;
import com.sofar.utility.DeviceUtil;
import com.sofar.utility.LogUtil;
import com.sofar.widget.highlight.BottomComponent;
import com.sofar.widget.highlight.Component;
import com.sofar.widget.highlight.Guide;
import com.sofar.widget.highlight.GuideBuilder;
import com.sofar.widget.highlight.GuideDialogFragment;
import com.sofar.widget.highlight.TopComponent;
import com.sofar.widget.progress.VoteProgress;

public class WidgetActivity extends AppCompatActivity {

  VoteProgress progress;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("控件测试页面");
    setContentView(R.layout.widget_activity);
    span1();
    span2();
    mask();
  }

  private void span1() {
    int padding = DeviceUtil.dp2px(this, 0);
    TextView span = findViewById(R.id.span1);
    String text = "我的图片小，文字大，图片和文字之间无间距";
    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
    drawable.setBounds(0, 0, DeviceUtil.dp2px(this, 28), DeviceUtil.dp2px(this, 10));
    SpannableString spannableString = SpanUtil.getLeftImageSpan(text, drawable, padding);
    span.setText(spannableString);
  }

  private void span2() {
    int padding = DeviceUtil.dp2px(this, 10);
    TextView span = findViewById(R.id.span2);
    String text = "我的文字大，图片小，图片和文字之间有间距";
    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
    drawable.setBounds(0, 0, DeviceUtil.dp2px(this, 28), DeviceUtil.dp2px(this, 28));
    SpannableString spannableString = SpanUtil.getLeftImageSpan(text, drawable, padding);
    span.setText(spannableString);
  }

  private void mask() {
    TextView mask = findViewById(R.id.mask);
    progress = findViewById(R.id.progress);
    //放大效果的组合动画
    AnimatorSet zoomInSet = new AnimatorSet();
    ObjectAnimator scaleXAnim1 = ObjectAnimator.ofFloat(mask, "scaleX", 1, 1.2f);
    ObjectAnimator scaleYAnim1 = ObjectAnimator.ofFloat(mask, "scaleY", 1, 1.2f);
    scaleXAnim1.setRepeatCount(ValueAnimator.INFINITE);
    scaleXAnim1.setRepeatMode(ValueAnimator.REVERSE);
    scaleYAnim1.setRepeatCount(ValueAnimator.INFINITE);
    scaleYAnim1.setRepeatMode(ValueAnimator.REVERSE);
    zoomInSet.setDuration(2000);
    //playTogether动画同时展示
    zoomInSet.playTogether(scaleXAnim1, scaleYAnim1);
    zoomInSet.addListener(new AnimatorListenerAdapter() {

      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        showMask(mask, scaleXAnim1, zoomInSet);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        LogUtil.d("WidgetActivity", "onAnimationEnd");
      }
    });
    mask.setOnClickListener(v -> {
      zoomInSet.start();
    });
  }

  private void showMask(View view, ValueAnimator animator, AnimatorSet animatorSet) {
    BottomComponent bottom = new BottomComponent("点我，弹窗引导");
    TopComponent top = new TopComponent("点我，转移引导目标");

    Guide guide = new GuideBuilder()
      .setTargetView(view)
      .setAlpha(100)
      .setHighTargetGraphStyle(Component.CIRCLE)
      .setHighTargetPadding(DeviceUtil.dp2px(this, 5))
      .addComponent(top)
      .addComponent(bottom)
      .setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
        @Override
        public void onShown() {

        }

        @Override
        public void onDismiss() {
          animatorSet.cancel();
        }
      })
      .createGuide();

    guide.show(this);

    int width = view.getWidth();
    animator.addUpdateListener(animation -> {
      float value = (float) animation.getAnimatedValue();
      int padding = (int) ((value - 1) * width / 2);
      guide.refreshTargetPadding(padding);
    });

    bottom.setOnClickListener(v -> {
      showDialog();
      guide.dismiss();
    });

    top.setOnClickListener(v -> {
      animatorSet.cancel();
      changeGuide(guide);
    });
  }

  private void changeGuide(Guide srcGuide) {
    Guide guide = new GuideBuilder()
      .setTargetView(progress)
      .setAlpha(100)
      .setHighTargetGraphStyle(Component.ROUNDRECT)
      .setHighTargetCorner(DeviceUtil.dp2px(this, 5))
      .setHighTargetPadding(DeviceUtil.dp2px(this, 5))
      .addComponent(new BottomComponent())
      .createGuide();

    guide.showWithGuide(srcGuide);
  }

  private void showDialog() {
    GuideDialogFragment dialog = new GuideDialogFragment();
    dialog.show(this.getSupportFragmentManager(), dialog.getTag());
  }
}
