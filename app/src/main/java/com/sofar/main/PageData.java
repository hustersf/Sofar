package com.sofar.main;

import java.util.ArrayList;
import java.util.List;

public class PageData {

  public String name;

  public String uri;

  public static List<PageData> buildPageDatas() {
    List<PageData> list = new ArrayList<>();
    list.add(PageData.createPageData("有趣的", "sofar://fun"));
    list.add(PageData.createPageData("换肤", "sofar://skin"));
    list.add(PageData.createPageData("控件", "sofar://widget"));
    list.add(PageData.createPageData("网络(RxJava)", "sofar://network"));
    list.add(PageData.createPageData("网络2(协程)", "sofar://network2"));
    list.add(PageData.createPageData("github仓库", "sofar://github"));
    list.add(PageData.createPageData("下载库", "sofar://download"));
    list.add(PageData.createPageData("预加载库", "sofar://preload"));
    list.add(PageData.createPageData("demo测试", "sofar://demo"));
    return list;
  }

  public static PageData createPageData(String name, String uri) {
    PageData data = new PageData();
    data.name = name;
    data.uri = uri;
    return data;
  }
}
