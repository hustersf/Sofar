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
    list.add(PageData.createPageData("网络", "sofar://network"));
    list.add(PageData.createPageData("github仓库", "sofar://github"));
    list.add(PageData.createPageData("播放器", "sofar://player"));
    list.add(PageData.createPageData("下载库", "sofar://download"));
    list.add(PageData.createPageData("预加载库", "sofar://preload"));
    return list;
  }

  public static PageData createPageData(String name, String uri) {
    PageData data = new PageData();
    data.name = name;
    data.uri = uri;
    return data;
  }
}
