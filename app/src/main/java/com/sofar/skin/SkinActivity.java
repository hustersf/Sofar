package com.sofar.skin;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
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

  ImageView skinColor1;
  ImageView skinColor2;
  ImageView skinColor3;
  ImageView skinColor4;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.skin_activity);
    skinLayout1 = findViewById(R.id.skin_background1);
    skinTv1 = findViewById(R.id.skin_text_color1);

    skinLayout2 = findViewById(R.id.skin_background2);
    skinTv2 = findViewById(R.id.skin_text_color2);
    skinSelectorTv2 = findViewById(R.id.skin_selector_text_color2);

    skinColor1 = findViewById(R.id.skin_color1);
    skinColor2 = findViewById(R.id.skin_color2);
    skinColor3 = findViewById(R.id.skin_color3);
    skinColor4 = findViewById(R.id.skin_color4);

    resourceTest();

    colorSkin();
  }

  private void resourceTest() {
    dynamicAddView(skinLayout2, "background", R.drawable.skin_background);
    dynamicAddView(skinTv2, "textColor", R.color.main_text_color);
    dynamicAddView(skinSelectorTv2, "textColor", R.color.skin_selector_color);
  }

  private void colorSkin() {
    int color1 = getResources().getColor(R.color.skin_color_1);
    GradientDrawable gradient1 = (GradientDrawable) skinColor1.getBackground();
    gradient1.setColor(color1);
    skinColor1.setOnClickListener(view -> {
      changeColorSkin(color1);
    });

    int color2 = getResources().getColor(R.color.skin_color_2);
    GradientDrawable gradient2 = (GradientDrawable) skinColor2.getBackground();
    gradient2.setColor(color2);
    skinColor2.setOnClickListener(view -> {
      changeColorSkin(color2);
    });

    int color3 = getResources().getColor(R.color.skin_color_3);
    GradientDrawable gradient3 = (GradientDrawable) skinColor3.getBackground();
    gradient3.setColor(color3);
    skinColor3.setOnClickListener(view -> {
      changeColorSkin(color3);
    });

    int color4 = getResources().getColor(R.color.skin_color_4);
    GradientDrawable gradient4 = (GradientDrawable) skinColor4.getBackground();
    gradient4.setColor(color4);
    skinColor4.setOnClickListener(view -> {
      changeColorSkin(color4);
    });
  }

  private void changeColorSkin(int color) {
    SkinManager.getInstance().loadColorSkin(color);
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
