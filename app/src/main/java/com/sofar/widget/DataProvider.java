package com.sofar.widget;

import java.util.ArrayList;
import java.util.List;

import com.sofar.fun.play.Feed;

public class DataProvider {

  /**
   * 简书图片地址后缀
   */
  public static final String JS_SUFFIX = "?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
  public static final String JS_GIF_SUFFIX = "?imageMogr2/auto-orient/strip";

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

  public final static String[] sGifUrls = new String[]{
    "https://upload-images.jianshu.io/upload_images/5352666-091c710e14b28e3f.gif",
    "https://upload-images.jianshu.io/upload_images/5352666-7ace945725eae0c0.gif",
    "https://upload-images.jianshu.io/upload_images/5352666-ff40bdc6dbe1a35b.gif",
  };

  public final static String[] sVideoUrls = new String[]{
    "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
  };

  public final static int[] sDrawableIds = new int[]{};

  public static List<String> imgUrls() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < sUrls.length; i++) {
      list.add(sUrls[i] + JS_SUFFIX);
    }
    return list;
  }

  public static List<String> gifUrls() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < sGifUrls.length; i++) {
      list.add(sGifUrls[i] + JS_GIF_SUFFIX);
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

  public static List<Feed> feeds() {
    List<Feed> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Feed feed = new Feed();
      if (i % 3 == 0) {
        feed.styleType = Feed.STYLE_PGC_BIG_CARD;
        feed.videoUrl = sVideoUrls[0];
      } else if (i % 3 == 1) {
        feed.styleType = Feed.STYLE_IMAGE_THREE_CARD;
        List<String> images = new ArrayList<>();
        images.addAll(gifUrls());
        feed.imgUrls = images;
      } else {
        feed.styleType = Feed.STYLE_IMAGE_BIG_CARD;
        if (i < sUrls.length) {
          feed.imgUrl = sUrls[i];
        }
      }
      list.add(feed);
    }
    return list;
  }
}
