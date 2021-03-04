package com.sofar.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.base.span.RoundBackgroundSpan;
import com.sofar.base.span.SpanUtil;
import com.sofar.utility.DeviceUtil;
import com.sofar.utility.LogUtil;
import com.sofar.utility.ToastUtil;
import com.sofar.utility.ViewUtil;
import com.sofar.widget.floating.FloatingWidget;
import com.sofar.widget.highlight.BottomComponent;
import com.sofar.widget.highlight.Component;
import com.sofar.widget.highlight.Guide;
import com.sofar.widget.highlight.GuideBuilder;
import com.sofar.widget.highlight.GuideDialogFragment;
import com.sofar.widget.highlight.TopComponent;
import com.sofar.widget.progress.VoteProgress;
import com.sofar.widget.recycler.ParentNoScrollRecyclerView;
import com.sofar.widget.recycler.StackCardAdapter;
import com.sofar.widget.recycler.layoutmanager.stack.StartMarginStackLayout;
import com.sofar.widget.recycler.layoutmanager.stack.StackLayoutManager;
import com.sofar.widget.swipe.SwipeBack;
import com.sofar.widget.swipe.SwipeLayout;

public class WidgetActivity extends AppCompatActivity {

  VoteProgress progress;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("控件测试页面");
    setContentView(R.layout.widget_activity);
    span1();
    span2();
    span3();
    mask();
    floatingWidget();
    swipe();
    overLayLayoutManager();
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

  private void span3() {
    TextView span = findViewById(R.id.span3);
    String text = "我是前面带标签的富文本，标签=背景+文字";
    String tag = "专题";
    SpannableString spannableString = new SpannableString(tag + text);
    RoundBackgroundSpan roundBackgroundSpan = new RoundBackgroundSpan();
    roundBackgroundSpan.setBgColor(Color.RED);
    roundBackgroundSpan.setTextColor(Color.WHITE);
    roundBackgroundSpan.setTextPadding(DeviceUtil.dp2px(this, 5), DeviceUtil.dp2px(this, 5));
    roundBackgroundSpan.setTextSize(DeviceUtil.dp2px(this, 11));
    roundBackgroundSpan.setSpanHeight(DeviceUtil.dp2px(this, 18));
    roundBackgroundSpan.setRadiusPx(DeviceUtil.dp2px(this, 3));
    roundBackgroundSpan.setRightMarginPx(DeviceUtil.dp2px(this, 5));
    spannableString.setSpan(roundBackgroundSpan, 0, tag.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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

    guide.setOnMaskClickListener((v, target) -> {
      if (target) {
        ToastUtil.startShort(this, "点击了高亮区域");
      }
    });
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

  private void floatingWidget() {
    FloatingWidget widget = new FloatingWidget(this);
    ViewUtil.inflate(widget, R.layout.read_timer, true);
    widget.findViewById(R.id.read_time_root).setOnClickListener(v -> {
      ToastUtil.startShort(this, "FloatingWidget");
    });
    widget.setScreenRatio(1.0f, 1.0f);
    new Handler().post(() -> {
      int actionBar = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
      widget.setInsets(0, 0, 0, actionBar);
      widget.attach();
    });
  }

  private void swipe() {
    SwipeBack.attach(this).setDirection(SwipeLayout.SwipeDirection.RIGHT);
  }

  private void overLayLayoutManager() {
    ParentNoScrollRecyclerView recyclerView = findViewById(R.id.over_lay_recycler);
    recyclerView.setDisallowOrientation(RecyclerView.HORIZONTAL);
    int loopCount = 200;
    StackCardAdapter adapter = new StackCardAdapter(loopCount);
    recyclerView.setAdapter(adapter);

    StackLayoutManager layoutManager = new StackLayoutManager();
    int itemOffset = DeviceUtil.dp2px(this, 8);
    int startMargin = DeviceUtil.dp2px(this, 15);
    int orientation = layoutManager.getScrollOrientation();
    int visibleItemCount = layoutManager.getVisibleItemCount();
    layoutManager.setLayout(new StartMarginStackLayout(orientation, visibleItemCount, itemOffset)
      .setStartMargin(startMargin));
    layoutManager.setItemOffset(itemOffset);
    recyclerView.setLayoutManager(layoutManager);

    List<Object> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(new Object());
    }
    adapter.setList(list);
    adapter.notifyDataSetChanged();

    recyclerView.post(() -> {
      recyclerView.scrollToPosition(list.size() * loopCount / 2);
    });
  }

}

