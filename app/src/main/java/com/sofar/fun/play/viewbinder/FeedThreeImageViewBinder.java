package com.sofar.fun.play.viewbinder;

import java.util.List;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.Feed;
import com.sofar.image.widget.SofarImageView;
import com.sofar.utility.CollectionUtil;

public class FeedThreeImageViewBinder extends RecyclerViewBinder<Feed> {

  SofarImageView cover1;
  SofarImageView cover2;
  SofarImageView cover3;
  Feed feed;

  @Override
  protected void onCreate() {
    super.onCreate();
    cover1 = view.findViewById(R.id.cover1);
    cover2 = view.findViewById(R.id.cover2);
    cover3 = view.findViewById(R.id.cover3);
  }

  @Override
  protected void onBind(Feed data) {
    super.onBind(data);
    feed = data;
    List<String> list = feed.imgUrls;
    if (!CollectionUtil.isEmpty(list)) {
      for (int i = 0; i < list.size(); i++) {
        String url = list.get(i);
        if (i == 0) {
          cover1.bindUrl(url);
        } else if (i == 1) {
          cover2.bindUrl(url);
        } else if (i == 2) {
          cover3.bindUrl(url);
        }
      }
    }
  }

}
