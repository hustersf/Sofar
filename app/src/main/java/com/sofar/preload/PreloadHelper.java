package com.sofar.preload;

import com.sofar.network.ApiProvider;
import com.sofar.preloader.interfaces.GroupedDataListener;
import com.sofar.preloader.interfaces.GroupedDataLoader;
import com.sofar.preloader.interfaces.PreloadListener;

import io.reactivex.Observable;

public class PreloadHelper {

  public static GroupedDataLoader<String> gitHubLoader() {
    return new GroupedDataLoader<String>() {
      @Override
      public String keyInGroup() {
        return "searchReposStr";
      }

      @Override
      public Observable<String> loader() {
        return ApiProvider.getGithubService().searchReposStr("Android", 0, 1);
      }
    };
  }

  public static GroupedDataListener<String> githubListener(PreloadListener<String> listener) {
    return new GroupedDataListener<String>() {
      @Override
      public String keyInGroup() {
        return "searchReposStr";
      }

      @Override
      public void onDataArrived(String s) {
        if (listener != null) {
          listener.onResponse(s);
        }
      }

      @Override
      public void onError(Throwable e) {
        if (listener != null) {
          listener.onError(e);
        }
      }
    };
  }

  public static GroupedDataLoader<String> bannerLoader() {
    return new GroupedDataLoader<String>() {
      @Override
      public String keyInGroup() {
        return "getBannerData";
      }

      @Override
      public Observable<String> loader() {
        return ApiProvider.getApiService().getBannerData();
      }
    };
  }


  public static GroupedDataListener<String> bannerListener(PreloadListener<String> listener) {
    return new GroupedDataListener<String>() {
      @Override
      public String keyInGroup() {
        return "getBannerData";
      }

      @Override
      public void onDataArrived(String s) {
        if (listener != null) {
          listener.onResponse(s);
        }
      }

      @Override
      public void onError(Throwable e) {
        if (listener != null) {
          listener.onError(e);
        }
      }
    };
  }
}
