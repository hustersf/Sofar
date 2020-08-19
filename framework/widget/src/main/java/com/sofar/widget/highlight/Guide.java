package com.sofar.widget.highlight;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

/**
 * 遮罩系统的封装 <br>
 * 外部需要调用{@link GuideBuilder}来创建该实例，实例创建后调用
 * {@link #show(Activity)} 控制显示； 调用 {@link #dismiss()}让遮罩系统消失。 <br>
 * <p>
 * Created by sufan
 */
public class Guide implements View.OnKeyListener, MaskView.OnMaskClickListener {

  Guide() {
  }

  private Configuration mConfiguration;
  private MaskView mMaskView;
  private ViewGroup mRootView;
  private Component[] mComponents;
  // 根据locInwindow定位后，是否需要判断loc值非0
  private boolean mShouldCheckLocInWindow = true;
  private GuideBuilder.OnVisibilityChangedListener mOnVisibilityChangedListener;

  private RectF mOriginRect = new RectF();
  private RectF mDstRect = new RectF();

  private MaskView.OnMaskClickListener mProxyMaskClickListener;

  void setConfiguration(Configuration configuration) {
    mConfiguration = configuration;
  }

  void setComponents(Component[] components) {
    mComponents = components;
  }

  void setCallback(GuideBuilder.OnVisibilityChangedListener listener) {
    mOnVisibilityChangedListener = listener;
  }

  /**
   * 显示该遮罩, <br>
   * 外部借助{@link GuideBuilder}
   * 创建好一个Guide实例后，使用该实例调用本函数遮罩才会显示
   *
   * @param activity 目标Activity
   */
  public void show(@NonNull Activity activity) {
    ViewGroup content = activity.findViewById(android.R.id.content);
    show(content);
  }

  /**
   * 两个guide 复用一个遮罩层，保证两个guide无缝切换（遮罩先消失，在展示）
   * 但测试发现，先消失在展示，也近似无缝切换
   *
   * @param srcGuide
   */
  public void showWithGuide(@NonNull Guide srcGuide) {
    this.mMaskView = srcGuide.mMaskView;
    this.mRootView = srcGuide.mRootView;
    if (mMaskView != null && mRootView != null) {
      initMaskView(mRootView, mMaskView);
    }
  }

  /**
   * 显示该遮罩
   * 外部借助{@link GuideBuilder}
   * 创建好一个Guide实例后，使用该实例调用本函数遮罩才会显示
   *
   * @param rootView 遮罩父View
   */
  public void show(@NonNull ViewGroup rootView) {
    mRootView = rootView;
    if (mMaskView == null) {
      mMaskView = new MaskView(rootView.getContext());
      initMaskView(rootView, mMaskView);
    }
    if (mMaskView.getParent() == null) {
      rootView.addView(mMaskView);
      if (mConfiguration.mEnterAnimationId != -1) {
        Animation anim =
          AnimationUtils.loadAnimation(rootView.getContext(), mConfiguration.mEnterAnimationId);
        assert anim != null;
        anim.setAnimationListener(new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {

          }

          @Override
          public void onAnimationEnd(Animation animation) {
            if (mOnVisibilityChangedListener != null) {
              mOnVisibilityChangedListener.onShown();
            }
          }

          @Override
          public void onAnimationRepeat(Animation animation) {

          }
        });
        mMaskView.startAnimation(anim);
      } else {
        if (mOnVisibilityChangedListener != null) {
          mOnVisibilityChangedListener.onShown();
        }
      }
    }
  }

  /**
   * 隐藏该遮罩并回收资源相关
   */
  public void dismiss() {
    if (mMaskView == null) {
      return;
    }
    final ViewGroup vp = (ViewGroup) mMaskView.getParent();
    if (vp == null) {
      return;
    }
    if (mConfiguration.mExitAnimationId != -1) {
      // mMaskView may leak if context is null
      Context context = mMaskView.getContext();
      assert context != null;

      Animation anim = AnimationUtils.loadAnimation(context, mConfiguration.mExitAnimationId);
      assert anim != null;
      anim.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          vp.removeView(mMaskView);
          if (mOnVisibilityChangedListener != null) {
            mOnVisibilityChangedListener.onDismiss();
          }
          onDestroy();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      mMaskView.startAnimation(anim);
    } else {
      vp.removeView(mMaskView);
      if (mOnVisibilityChangedListener != null) {
        mOnVisibilityChangedListener.onDismiss();
      }
      onDestroy();
    }
  }

  /**
   * 根据locInwindow定位后，是否需要判断loc值非0
   * 主要看我们的页面是从最顶部开始，还是从状态栏以下
   * true，页面从状态栏以下开始计算
   * false，页面从最顶部开始计算
   */
  public void setShouldCheckLocInWindow(boolean set) {
    mShouldCheckLocInWindow = set;
  }

  /**
   * 当targetView 大小动态变化时，高亮区域跟着变化，暂时只支持 同比例缩放
   *
   * @param padding 假设初始宽高为50，变化后为60，则padding=(60-50)/2
   */
  public void refreshTargetPadding(int padding) {
    mDstRect.set(mOriginRect);
    mDstRect.left -= padding;
    mDstRect.top -= padding;
    mDstRect.right += padding;
    mDstRect.bottom += padding;
    if (mMaskView != null) {
      mMaskView.setTargetRect(mDstRect);
    }
  }

  /**
   * mOutsideTouchable 为false时有效
   */
  public void setOnMaskClickListener(MaskView.OnMaskClickListener listener) {
    this.mProxyMaskClickListener = listener;
  }

  private void initMaskView(@NonNull ViewGroup rootView, @NonNull MaskView maskView) {
    maskView.setFullingColor(
      rootView.getContext().getResources().getColor(mConfiguration.mFullingColorId));
    maskView.setFullingAlpha(mConfiguration.mAlpha);
    maskView.setHighTargetCorner(mConfiguration.mCorner);
    maskView.setPadding(mConfiguration.mPadding);
    maskView.setPaddingLeft(mConfiguration.mPaddingLeft);
    maskView.setPaddingTop(mConfiguration.mPaddingTop);
    maskView.setPaddingRight(mConfiguration.mPaddingRight);
    maskView.setPaddingBottom(mConfiguration.mPaddingBottom);
    maskView.setHighTargetGraphStyle(mConfiguration.mGraphStyle);
    maskView.setOverlayTarget(mConfiguration.mOverlayTarget);
    maskView.setDashedDecoration(mConfiguration.mShowDecoration);
    maskView.setTargetViewRectMax(mConfiguration.mTargetViewRectMax);
    maskView.setOnKeyListener(this);

    // For removing the height of status bar we need the root content view's
    // location on screen
    int parentX = 0;
    int parentY = 0;
    final int[] loc = new int[2];
    rootView.getLocationInWindow(loc);
    parentY = loc[1];// 通知栏的高度
    if (mShouldCheckLocInWindow && parentY == 0) {
      Class<?> localClass;
      try {
        localClass = Class.forName("com.android.internal.R$dimen");
        Object localObject = localClass.newInstance();
        int i5 =
          Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
        parentY = rootView.getContext().getResources().getDimensionPixelSize(i5);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (mConfiguration.mTargetView != null) {
      mOriginRect.set(Common.getViewAbsRect(mConfiguration.mTargetView, parentX, parentY));
    } else {
      // Gets the target view's abs rect
      View target = rootView.findViewById(mConfiguration.mTargetViewId);
      if (target != null) {
        mOriginRect.set(Common.getViewAbsRect(target, parentX, parentY));
      }
    }
    maskView.setTargetRect(mOriginRect);

    if (mConfiguration.mOutsideTouchable) {
      maskView.setClickable(false);
    } else {
      maskView.setOnMaskClickListener(this);
    }

    // Adds the components to the mask view.
    maskView.removeAllViews();
    for (Component c : mComponents) {
      maskView.addView(Common.componentToView(LayoutInflater.from(rootView.getContext()), c));
    }
  }

  private void onDestroy() {
    mConfiguration = null;
    mComponents = null;
    mOnVisibilityChangedListener = null;
    mMaskView.removeAllViews();
    mMaskView = null;
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
      if (mConfiguration != null && mConfiguration.mAutoDismiss) {
        dismiss();
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  @Override
  public void onClick(View v, boolean target) {
    if (mConfiguration != null && mConfiguration.mAutoDismiss) {
      dismiss();
      if (mProxyMaskClickListener != null) {
        mProxyMaskClickListener.onClick(v, target);
      }
    }
  }
}
