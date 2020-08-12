package com.sofar.widget.highlight;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.R;
import com.sofar.fun.dialog.SofarDialogFragment;
import com.sofar.utility.DeviceUtil;
import com.sofar.utility.statusbar.StatusBarUtil;

public class GuideDialogFragment extends SofarDialogFragment {

  View dialogView;
  TextView cancelTv;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.highlight_dialog, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setDimBackgroundEnabled(false);
    setWrapContentHeight(false);
    setWindowContentHeight(DeviceUtil.getMetricsHeight(getActivity()) - StatusBarUtil.getStatusBarHeight(getActivity()));
    setGravity(Gravity.BOTTOM);

    dialogView = view.findViewById(R.id.root);
    cancelTv = view.findViewById(R.id.cancel);

    cancelTv.setOnClickListener(v -> {
      dismiss();
    });

    view.postDelayed(new Runnable() {
      @Override
      public void run() {
        showMask();
      }
    }, 0);
  }

  private void showMask() {
    if (getActivity() == null) {
      return;
    }

    Guide guide = new GuideBuilder()
      .setTargetView(dialogView)
      .setAlpha(100)
      .setOutsideTouchable(true)
      .setHighTargetGraphStyle(Component.ROUNDRECT)
      .setHighTargetPadding(DeviceUtil.dp2px(getActivity(), 5))
      .setHighTargetCorner(DeviceUtil.dp2px(getActivity(), 5))
      .addComponent(new TopComponent())
      .createGuide();

    guide.setShouldCheckLocInWindow(false);
    if (getView() instanceof ViewGroup) {
      guide.show((ViewGroup) getView());
    }
  }
}
