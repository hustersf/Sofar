package com.sofar.image;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoUtil {

  static Handler mainHandler = new Handler(Looper.getMainLooper());

  public static void fetchImage(String url, ImageCallback callback) {
    if (url == null || callback == null) {
      return;
    }

    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
    DataSource<CloseableReference<CloseableImage>> dataSource =
      Fresco.getImagePipeline().fetchDecodedImage(imageRequest, null);
    dataSource.subscribe(new BaseBitmapDataSubscriber() {
      @Override
      protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

      }

      @Override
      protected void onNewResultImpl(Bitmap bitmap) {
        mainHandler.post(() -> {
          if (callback != null) {
            callback.onSuccess(bitmap);
          }
        });
      }

    }, CallerThreadExecutor.getInstance());
  }
}
