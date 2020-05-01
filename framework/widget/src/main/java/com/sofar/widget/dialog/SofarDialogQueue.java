package com.sofar.widget.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 弹窗队列管理，保证一次只能弹出一个
 */
public class SofarDialogQueue {

  private WeakHashMap<FragmentManager, List<SofarDialogFragment>> dialogMap = new WeakHashMap<>();

  private SofarDialogQueue() {
  }

  private static class HolderClass {
    private static SofarDialogQueue instance = new SofarDialogQueue();
  }

  public static SofarDialogQueue get() {
    return HolderClass.instance;
  }

  public void show(@NonNull FragmentActivity activity, @NonNull SofarDialogFragment dialog) {
    if (activity == null || activity.isFinishing()) {
      return;
    }

    FragmentManager fragmentManager = activity.getSupportFragmentManager();
    List<SofarDialogFragment> list = dialogMap.get(fragmentManager);
    if (list == null) {
      list = new ArrayList<>();
      dialogMap.put(fragmentManager, list);
    }

    if (list.contains(dialog)) {
      return;
    }

    if (!list.isEmpty()) {
      list.add(dialog);
    } else {
      list.add(dialog);
      realShow(fragmentManager, dialog);
    }
  }

  private void realShow(@NonNull FragmentManager manager, @NonNull SofarDialogFragment dialog) {
    try {
      dialog.show(manager, dialog.getTag());
      dialog.setOnDismissListener(d -> {
        List<SofarDialogFragment> list = dialogMap.get(manager);
        if (list != null) {
          list.remove(dialog);
        }
        popShowDialogFragment(manager);
      });
    } catch (Exception e) {
    }
  }

  private void popShowDialogFragment(@NonNull FragmentManager manager) {
    List<SofarDialogFragment> list = dialogMap.get(manager);
    if (list != null && list.size() > 0) {
      SofarDialogFragment dialog = getFirstDialogFragment(list);
      if (dialog != null) {
        realShow(manager, dialog);
      }
    }
  }

  @Nullable
  private SofarDialogFragment getFirstDialogFragment(@NonNull List<SofarDialogFragment> list) {
    for (SofarDialogFragment dialog : list) {
      if (dialog != null) {
        return dialog;
      }
    }
    return null;
  }

}
