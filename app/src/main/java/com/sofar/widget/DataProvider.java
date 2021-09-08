package com.sofar.widget;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {

  /**
   * 简书图片地址后缀
   */
  public static final String JS_SUFFIX = "?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";

  public final static String[] sUrls = new String[]{
    "https://upload-images.jianshu.io/upload_images/5352666-93b3df0c4e715930.jpg",
    "https://upload-images.jianshu.io/upload_images/5352666-b39738edbec0794c.jpg",
    "https://upload-images.jianshu.io/upload_images/5352666-9b42ca771fa94214.jpg",
    "https://upload-images.jianshu.io/upload_images/5352666-6c5b34acd2ca97e4.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-40a3593c856eec26.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-7f906cc6cf4a3980.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-44f3e9c06c9b6681.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-a4fb08455f7c26b3.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-fa83449629b7af02.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-6ed075e292940d6d.jpeg",
    "https://upload-images.jianshu.io/upload_images/5352666-11923e156a78f838.jpeg",
  };

  public final static int[] sDrawableIds = new int[]{};

  public static List<String> imgUrls() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < sUrls.length; i++) {
      list.add(sUrls[i] + JS_SUFFIX);
    }
    return list;
  }

  public static List<Integer> drawableIds() {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < sDrawableIds.length; i++) {
      list.add(sDrawableIds[i]);
    }
    return list;
  }
}
