package com.sofar.skin.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sofar.skin.callback.IDynamicNewView;
import com.sofar.skin.model.DynamicAttr;
import com.sofar.skin.util.SkinL;

import java.util.List;

public class SkinBaseFragment extends Fragment implements IDynamicNewView {

  SkinBaseActivity activity;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof SkinBaseActivity) {
      activity = (SkinBaseActivity) context;
    }
  }

  @Override
  public void dynamicAddView(View view, List<DynamicAttr> dynamicAttrs) {
    if (activity != null) {
      activity.dynamicAddView(view, dynamicAttrs);
    } else {
      SkinL.e("SkinBaseFragment must attach SkinBaseActivity");
    }
  }

  @Override
  public void dynamicAddView(View view, String attrName, int attrValueResId) {
    if (activity != null) {
      activity.dynamicAddView(view, attrName, attrValueResId);
    } else {
      SkinL.e("SkinBaseFragment must attach SkinBaseActivity");
    }
  }
}
