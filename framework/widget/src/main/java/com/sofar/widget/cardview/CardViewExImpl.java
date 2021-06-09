package com.sofar.widget.cardview;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.Nullable;

public interface CardViewExImpl {

  void initialize(CardViewExDelegate cardView, Context context, ColorStateList backgroundColor,
    float radius, float elevation, float maxElevation, int shadowStartColor, int shadowEndColor);

  void setRadius(CardViewExDelegate cardView, float radius);

  float getRadius(CardViewExDelegate cardView);

  void setElevation(CardViewExDelegate cardView, float elevation);

  float getElevation(CardViewExDelegate cardView);

  void initStatic();

  void setMaxElevation(CardViewExDelegate cardView, float maxElevation);

  float getMaxElevation(CardViewExDelegate cardView);

  float getMinWidth(CardViewExDelegate cardView);

  float getMinHeight(CardViewExDelegate cardView);

  void updatePadding(CardViewExDelegate cardView);

  void onCompatPaddingChanged(CardViewExDelegate cardView);

  void onPreventCornerOverlapChanged(CardViewExDelegate cardView);

  void setBackgroundColor(CardViewExDelegate cardView, @Nullable ColorStateList color);

  ColorStateList getBackgroundColor(CardViewExDelegate cardView);

}
