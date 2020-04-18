package com.sofar.main;

import java.util.ArrayList;
import java.util.List;

public class PageData {

  public String name;

  public String uri;

  public static List<PageData> buildPageDatas() {
    List<PageData> list = new ArrayList<>();
    list.add(PageData.createPageData("换肤", "sofar://skin"));
    list.add(PageData.createPageData("控件", "sofar://widget"));
    return list;
  }

  public static PageData createPageData(String name, String uri) {
    PageData data = new PageData();
    data.name = name;
    data.uri = uri;
    return data;
  }
}
