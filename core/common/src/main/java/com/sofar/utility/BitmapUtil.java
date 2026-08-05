package com.sofar.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BitmapUtil {

  /**
   * @param bitmap
   * @return 圆形图片
   */
  public static Bitmap getOvalBitmap(Bitmap bitmap) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
      .getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

    final RectF rectF = new RectF(rect);

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);

    canvas.drawOval(rectF, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    return output;
  }


  /**
   * @param bitmap
   * @param roundPx
   * @return 得到圆角图片
   */
  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
      .getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }


  /**
   * Bitmap压缩到指定的千字节数（比方说图片要压缩成32K，则传32）
   * 质量压缩
   *
   * @param srcBitmap
   * @param maxKByteCount 比方说图片要压缩成32K，则传32
   * @return
   */
  public static Bitmap compressBitmap(Bitmap srcBitmap, int maxKByteCount) {
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      int option = 90;
      while (baos.toByteArray().length / 1024 >= maxKByteCount && option > 0) {
        baos.reset();
        srcBitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
        option -= 10;
      }
    } catch (Exception e) {

    }
    // 把压缩后的数据baos存放到ByteArrayInputStream中
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    // 把ByteArrayInputStream数据生成图片
    Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);
    return bitmap;
  }


  /**
   * 将图片压缩到指定大小（比例压缩）
   */
  public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();

    float scaleWidth = ((float) w) / width;
    float scaleHeight = ((float) h) / height;

    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);

    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
      height, matrix, true);
    return resizedBitmap;
  }

  public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
    if (origin == null) {
      return null;
    }
    int width = origin.getWidth();
    int height = origin.getHeight();
    Matrix matrix = new Matrix();
    matrix.setRotate(alpha);
    // 围绕原地进行旋转
    Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    // origin.recycle();
    return newBM;
  }
}
