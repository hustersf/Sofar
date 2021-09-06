package com.sofar.image.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.postprocessors.BlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

/**
 * 提供图片 绑定url 的方法
 */
public class SofarImageView extends SimpleDraweeView {

  private int rotation;

  public SofarImageView(Context context, GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
  }

  public SofarImageView(Context context) {
    super(context);
  }

  public SofarImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SofarImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  /**
   * @param rotation 设置图片旋转角度
   */
  public void setImageRotation(int rotation) {
    this.rotation = rotation;
  }

  /**
   * @param url 图片地址
   */
  public void bindUrl(@Nullable String url) {
    bindUrl(url, null);
  }

  public void bindUrl(@Nullable String url, ControllerListener<ImageInfo> listener) {
    bindUrl(url, listener, true);
  }

  /**
   * @param url    图片地址
   * @param rgb565 是否使用rgb565 （更省内存）
   */
  public void bindUrl(@Nullable String url, ControllerListener<ImageInfo> listener, boolean rgb565) {
    if (url == null) {
      setController(null);
    } else {
      bindUri(Uri.parse(url), 0, 0, null, listener, rgb565);
    }
  }

  /**
   * @param uri          支持多种绝对路径 包括网络请求, 本地文件, asset文件, res文件
   *                     Uri uri = Uri.parse("res://包名(实际可以是任何字符串甚至留空)/" + R.drawable.ic_launcher);
   * @param resizeWidth  需要resize的宽度
   * @param resizeHeight 需要resize的高度
   * @param processor    图片处理逻辑，如高斯模糊{@link BlurPostProcessor}
   * @param listener     监听图片加载的各个阶段
   * @param rgb565       是否使用rgb565
   */
  public void bindUri(@NonNull Uri uri, int resizeWidth, int resizeHeight, Postprocessor processor,
                      ControllerListener<ImageInfo> listener, boolean rgb565) {
    ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
    builder.setPostprocessor(processor);
    if (resizeWidth > 0 && resizeHeight > 0) {
      builder.setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight));
    }

    if (rotation > -1) {
      builder.setRotationOptions(RotationOptions.forceRotation(rotation));
    }

    if (rgb565) {
      ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
        .setBitmapConfig(Bitmap.Config.RGB_565)
        .build();
      builder.setImageDecodeOptions(decodeOptions);
    }

    DraweeController controller = Fresco.newDraweeControllerBuilder()
      .setOldController(getController())
      .setImageRequest(builder.build())
      .setControllerListener(listener)
      .build();
    setController(controller);
  }

}
