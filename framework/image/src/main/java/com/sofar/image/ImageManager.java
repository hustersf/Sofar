package com.sofar.image;

import android.app.Application;
import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

public class ImageManager {

  Context appContext;

  private final String cacheFileName = "image_cache";

  private ImageManager() {
  }

  private static class Inner {
    private static ImageManager INSTANCE = new ImageManager();
  }

  public static ImageManager get() {
    return Inner.INSTANCE;
  }

  public void init(Application appContext) {
    this.appContext = appContext;
    //磁盘配置
    DiskCacheConfig cacheConfig = DiskCacheConfig.newBuilder(appContext)
      .setBaseDirectoryPath(Util.getCacheDir(appContext))
      .setBaseDirectoryName(cacheFileName)
      .build();
    ImagePipelineConfig pipelineConfig = ImagePipelineConfig.newBuilder(appContext)
      .setMainDiskCacheConfig(cacheConfig)
      .build();
    Fresco.initialize(appContext, pipelineConfig);
  }

  public Context getAppContext() {
    return appContext;
  }
}
