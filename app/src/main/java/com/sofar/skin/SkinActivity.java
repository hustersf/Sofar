package com.sofar.skin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.R;
import com.sofar.skin.base.SkinBaseActivity;
import com.sofar.skin.callback.ILoaderListener;
import com.sofar.skin.core.SkinManager;
import com.sofar.utility.LogUtil;

public class SkinActivity extends SkinBaseActivity {

  private static String TAG = "SkinActivity";

  LinearLayout skinLayout1;
  TextView skinTv1;

  LinearLayout skinLayout2;
  TextView skinTv2;
  TextView skinSelectorTv2;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.skin_activity);
    skinLayout1 = findViewById(R.id.skin_background1);
    skinTv1 = findViewById(R.id.skin_text_color1);

    skinLayout2 = findViewById(R.id.skin_background2);
    skinTv2 = findViewById(R.id.skin_text_color2);
    skinSelectorTv2 = findViewById(R.id.skin_selector_text_color2);

    resourceTest();
  }

  private void resourceTest() {
    dynamicAddView(skinLayout2, "background", R.drawable.skin_background);
    dynamicAddView(skinTv2, "textColor", R.color.main_text_color);
    dynamicAddView(skinSelectorTv2, "textColor", R.color.skin_selector_color);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.skin_item_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.skin1:
        changeSkin("red.skin");
        break;
      case R.id.skin2:
        changeSkin("green.skin");
        break;
      case R.id.skin3:
        changeSkin("blue.skin");
        break;
      case R.id.skin4:
        SkinManager.getInstance().restoreDefaultTheme();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void changeSkin(String skinName) {

    SkinManager.getInstance().loadSkin(skinName, new ILoaderListener() {
      @Override
      public void onStart() {

      }

      @Override
      public void onSuccess() {
        LogUtil.d(TAG, "onSuccess");
      }

      @Override
      public void onFailed(String errMsg) {
        LogUtil.d(TAG, "onFailed:" + errMsg);
      }

      @Override
      public void onProgress(int progress) {

      }
    });

  }
}
