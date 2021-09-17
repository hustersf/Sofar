package com.sofar.fun.play;

import java.util.List;

public class Feed {

  public static final int STYLE_IMAGE_BIG_CARD = 200; // 图文大卡
  public static final int STYLE_IMAGE_THREE_CARD = 201; // 三图卡片

  public static final int STYLE_PGC_BIG_CARD = 300; // 视频大卡

  public int styleType;

  public String title;

  public String imgUrl;

  public String videoUrl;

  public List<String> imgUrls;

  public int drawableId;
}
