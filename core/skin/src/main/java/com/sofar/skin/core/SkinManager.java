package com.sofar.skin.core;

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

import com.sofar.skin.callback.ILoaderListener;
import com.sofar.skin.callback.ISkinLoader;
import com.sofar.skin.callback.ISkinUpdate;
import com.sofar.skin.config.SkinConfig;
import com.sofar.skin.util.SkinFileUtil;
import com.sofar.skin.util.SkinL;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SkinManager implements ISkinLoader {

  private Context context;
  private Resources resources;  //皮肤包的resources对象
  private boolean defaultSkin; //当前的皮肤是否是默认的
  private String skinPackageName; //皮肤apk的包名

  private boolean colorSkin;  //当前皮肤是否是纯颜色换肤

  private List<ISkinUpdate> skinObservers;

  @Override
  public void attach(ISkinUpdate observer) {
    if (skinObservers == null) {
      skinObservers = new ArrayList<>();
    }
    if (!skinObservers.contains(observer)) {
      skinObservers.add(observer);
    }
  }

  @Override
  public void detach(ISkinUpdate observer) {
    if (skinObservers == null) {
      return;
    }
    if (skinObservers.contains(observer)) {
      skinObservers.remove(observer);
    }
  }

  @Override
  public void notifySkinUpdate() {
    if (skinObservers == null) {
      return;
    }
    for (ISkinUpdate observer : skinObservers) {
      observer.onThemeUpdate();
    }
  }

  private static class HolderClass {
    private static SkinManager instance = new SkinManager();
  }

  public static SkinManager getInstance() {
    return HolderClass.instance;
  }

  public void init(@NonNull Context context) {
    this.context = context.getApplicationContext();
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

  /**
   * 恢复至默认皮肤
   */
  public void restoreDefaultTheme() {
    SkinConfig.saveSkinName(context, "");
    defaultSkin = true;
    colorSkin = false;
    resources = context.getResources();
    skinPackageName = context.getPackageName();
    notifySkinUpdate();
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
    notifySkinUpdate();
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
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
            skinPackageName = packageInfo.packageName;
            SkinL.d("skin packageName:" + skinPackageName);

            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, skinPkgPath);

            Resources superRes = context.getResources();
            Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
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
          notifySkinUpdate();
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

    Uri downloadUri = Uri.parse(skinUrl);
    Uri destinationUri = Uri.parse(skinPkgPath);
    DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
      .setRetryPolicy(new DefaultRetryPolicy())
      .setDestinationURI(destinationUri)
      .setPriority(DownloadRequest.Priority.HIGH);
    if (listener != null) {
      listener.onStart();
    }
    downloadRequest.setStatusListener(new DownloadStatusListenerV1() {
      @Override
      public void onDownloadComplete(DownloadRequest downloadRequest) {
        loadSkin(skinName, listener);
      }

      @Override
      public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
        if (listener != null) {
          listener.onFailed("download skin failed=" + errorCode + ":" + errorMessage);
        }
      }

      @Override
      public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
        if (listener != null) {
          listener.onProgress(progress);
        }
      }
    });

    ThinDownloadManager manager = new ThinDownloadManager();
    manager.add(downloadRequest);
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

}
