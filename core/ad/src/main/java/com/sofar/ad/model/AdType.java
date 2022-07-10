package com.sofar.ad.model;

import androidx.annotation.StringDef;

@StringDef({AdType.FEED, AdType.REWARD, AdType.SPLASH, AdType.FULLSCREEN, AdType.BANNER})
public @interface AdType {

  String FEED = "feed";

  String REWARD = "reward";

  String SPLASH = "splash";

  String FULLSCREEN = "fullscreen";

  String BANNER = "banner";

}
