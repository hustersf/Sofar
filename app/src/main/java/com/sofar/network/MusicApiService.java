package com.sofar.network;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface MusicApiService {

  /**
   * 获取榜单列表
   */
  @GET("v1/restserver/ting?method=baidu.ting.billboard.billCategory")
  Observable<String> rankList();
}
