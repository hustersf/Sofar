package com.sofar.skin.core;

import java.io.File;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.skin.callback.ILoaderListener;
import com.sofar.skin.config.SkinConfig;
import com.sofar.skin.util.SkinFileUtil;
import com.sofar.skin.util.SkinL;
import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

/**
 * 管理皮肤包资源
 */
public class SkinResourceManager {

  private Context context;
  private Resources resources;  //皮肤包的resources对象
  private boolean defaultSkin; //当前的皮肤是否是默认的
  private String skinPackageName; //皮肤apk的包名
  private boolean colorSkin;  //当前皮肤是否是纯颜色换肤

  private Fetch fetch;

  private SkinResourceManager() {
  }

  private static class Inner {
    private static SkinResourceManager INSTANCE = new SkinResourceManager();
  }

  public static SkinResourceManager get() {
    return Inner.INSTANCE;
  }

  public void init(@NonNull Context context) {
    this.context = context.getApplicationContext();
    FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
      .setDownloadConcurrentLimit(3)
      .setHttpDownloader(new OkHttpDownloader())
      .build();
    fetch = Fetch.Impl.getInstance(fetchConfiguration);
    setUpSkinFile();
    loadSkin();
  }

  /**
   * 将assets目录下的皮肤包拷贝到手机目录下
   */
  private void setUpSkinFile() {
    SkinL.d("start prepare skin file");
    try {
      String[] skinNames = context.getAssets().list(SkinConfig.SKIN_DIR_NAME);
      if (skinNames == null || skinNames.length == 0) {
        SkinL.d("assets/skin has no skin");
        return;
      }

      for (String skinName : skinNames) {
        File file = new File(SkinFileUtil.getSkinDir(context), skinName);
        if (!file.exists()) {
          SkinFileUtil.copySkinAssetsToDir(context, skinName, SkinFileUtil.getSkinDir(context));
          SkinL.d("copy skin from assets to local file:" + skinName);
        } else {
          SkinL.d(skinName + " had copy to local file");
        }
      }
    } catch (Exception e) {
      String errorMsg = "unknown";
      if (e != null) {
        errorMsg = e.toString();
      }
      SkinL.d("setUpSkinFile failed:" + errorMsg);
    }
  }

  /**
   * 加载皮肤包的资源
   */
  private void loadSkin() {
    String skinName = SkinConfig.getSkinName(context);
    if (SkinConfig.SKIN_COLOR_NAME.equals(skinName)) {
      SkinL.d("init load color skin");
      loadColorSkin(SkinConfig.getSkinColorValue(context));
    } else if (!TextUtils.isEmpty(skinName)) {
      SkinL.d("init load skin:" + skinName);
      loadSkin(skinName, null);
    } else {
      SkinL.d("app use no skin");
    }
  }

  /**
   * 纯颜色换肤
   */
  public void loadColorSkin(int color) {
    SkinL.d("load color skin:" + Integer.toHexString(color));
    SkinConfig.SKIN_COLOR_VALUE = color;
    SkinConfig.saveSkinName(context, SkinConfig.SKIN_COLOR_NAME);
    SkinConfig.saveSkinColorValue(context, color);
    defaultSkin = false;
    colorSkin = true;
    resources = context.getResources();
    skinPackageName = context.getPackageName();
    SkinObserverManager.get().notifySkinUpdate();
  }

  /**
   * load skin form local(in assets)
   *
   * @param skinName the name of skin(in assets/skin)
   * @param listener load callback
   */
  public void loadSkin(final String skinName, final ILoaderListener listener) {
    new AsyncTask<String, Void, Resources>() {

      @Override
      protected void onPreExecute() {
        if (listener != null) {
          listener.onStart();
        }
      }

      @Override
      protected Resources doInBackground(String... params) {
        try {
          if (params.length == 1) {
            String skinPkgPath = SkinFileUtil.getSkinDir(context) + File.separator + params[0];
            SkinL.d("start load skin path:" + skinPkgPath);

            File file = new File(skinPkgPath);
            if (!file.exists()) {
              SkinL.d("skin file not exist");
              return null;
            }

            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo =
              packageManager.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
            skinPackageName = packageInfo.packageName;
            SkinL.d("skin packageName:" + skinPackageName);

            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, skinPkgPath);

            Resources superRes = context.getResources();
            Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(),
              superRes.getConfiguration());
            return skinResource;
          }
        } catch (Exception e) {
          String errorMsg = "unknown";
          if (e != null) {
            errorMsg = e.toString();
          }
          SkinL.d("load skin resources failed:" + errorMsg);
        }
        return null;
      }

      @Override
      protected void onPostExecute(Resources result) {
        resources = result;
        if (resources != null) {
          defaultSkin = false;
          colorSkin = false;
          SkinConfig.saveSkinName(context, skinName);
          if (listener != null) {
            listener.onSuccess();
          }
          SkinObserverManager.get().notifySkinUpdate();
        } else {
          defaultSkin = true;
          if (listener != null) {
            listener.onFailed("load skin resources failed");
          }
        }
      }
    }.execute(skinName);
  }

  /**
   * load skin form internet
   *
   * @param skinUrl  the url of skin file
   * @param listener load callback
   */
  public void loadSkinFromUrl(@NonNull String skinUrl, final ILoaderListener listener) {
    final String skinName = skinUrl.substring(skinUrl.lastIndexOf("/") + 1);
    String skinPkgPath = SkinFileUtil.getSkinDir(context) + File.separator + skinName;
    File file = new File(skinPkgPath);
    if (file.exists()) {
      loadSkin(skinName, listener);
      SkinL.d("skinUrl has download");
      return;
    }

    Uri destinationUri = Uri.parse(skinPkgPath);
    Request request = new Request(skinUrl, destinationUri);
    request.setPriority(Priority.HIGH);
    request.setNetworkType(NetworkType.ALL);
    if (listener != null) {
      listener.onStart();
    }
    FetchListener fetchListener = new AbstractFetchListener() {
      @Override
      public void onCompleted(@NonNull Download download) {
        super.onCompleted(download);
        loadSkin(skinName, listener);
        fetch.removeListener(this);
      }

      @Override
      public void onError(@NonNull Download download, @NonNull Error error,
        @Nullable Throwable throwable) {
        super.onError(download, error, throwable);
        if (listener != null) {
          listener.onFailed("download skin failed=" + error);
        }
      }

      @Override
      public void onProgress(@NonNull Download download, long etaInMilliSeconds,
        long downloadedBytesPerSecond) {
        super.onProgress(download, etaInMilliSeconds, downloadedBytesPerSecond);
        int progress = download.getProgress();
        if (listener != null) {
          listener.onProgress(progress);
        }
      }
    };
    fetch.addListener(fetchListener);
    fetch.enqueue(request, updatedRequest -> {
      SkinL.d("Fetch", "任务ID：" + updatedRequest.getId());
    }, error -> {
      SkinL.e("Fetch", "任务创建失败：" + error.toString());
    });
  }

  public void restoreDefaultSkin() {
    SkinConfig.saveSkinName(context, "");
    defaultSkin = true;
    colorSkin = false;
    resources = context.getResources();
    skinPackageName = context.getPackageName();
    SkinObserverManager.get().notifySkinUpdate();
  }

  public int getColor(int resId) {
    String resName = context.getResources().getResourceEntryName(resId);
    if (colorSkin && SkinColorWhiteList.supportSkinColorResNames.contains(resName)) {
      if (SkinConfig.SKIN_COLOR_VALUE == -1) {
        SkinConfig.SKIN_COLOR_VALUE = SkinConfig.getSkinColorValue(context);
      }
      return SkinConfig.SKIN_COLOR_VALUE;
    }

    int originColor = context.getResources().getColor(resId);
    if (resources == null || defaultSkin) {
      return originColor;
    }

    int skinResId = resources.getIdentifier(resName, "color", skinPackageName);
    int skinColor;
    try {
      skinColor = resources.getColor(skinResId);
    } catch (Resources.NotFoundException e) {
      skinColor = originColor;
      SkinL.d(resName + " not found in skin package:" + SkinConfig.getSkinName(context));
    }
    return skinColor;
  }

  public Drawable getDrawable(int resId) {
    Drawable originDrawable = context.getResources().getDrawable(resId);
    if (resources == null || defaultSkin) {
      return originDrawable;
    }

    String resName = context.getResources().getResourceEntryName(resId);
    int skinResId = resources.getIdentifier(resName, "drawable", skinPackageName);
    Drawable skinDrawable;
    try {
      skinDrawable = resources.getDrawable(skinResId);
    } catch (Resources.NotFoundException e) {
      skinDrawable = originDrawable;
      SkinL.d(resName + " not found in skin package:" + SkinConfig.getSkinName(context));
    }
    return skinDrawable;
  }

  public ColorStateList getColorStateList(int resId) {
    ColorStateList originColorStateList = context.getResources().getColorStateList(resId);
    if (resources == null || defaultSkin) {
      return originColorStateList;
    }

    String resName = context.getResources().getResourceEntryName(resId);
    int skinResId = resources.getIdentifier(resName, "color", skinPackageName);
    ColorStateList skinColorStateList;
    try {
      skinColorStateList = resources.getColorStateList(skinResId);
    } catch (Resources.NotFoundException e) {
      skinColorStateList = originColorStateList;
      SkinL.d(resName + " not found in skin package:" + SkinConfig.getSkinName(context));
    }
    return skinColorStateList;
  }

  /**
   * 判断当前使用的皮肤是否来自外部
   */
  public boolean isExternalSkin() {
    return !defaultSkin && resources != null;
  }

  /**
   * 判断当前使用的皮肤是否纯颜色换肤
   */
  public boolean isColorSkin() {
    return colorSkin;
  }
}
