package com.sofar.fun.play;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

import com.sofar.fun.play.card.FeedImageBigCard;
import com.sofar.fun.play.card.FeedImageThreePlayCard;
import com.sofar.fun.play.card.FeedItemCard;
import com.sofar.fun.play.card.FeedPgcBigCard;

public enum FeedViewType {

  TYPE_KEY_PGC_BIG_CARD,
  TYPE_KEY_IMAGE_BIG_CARD,
  TYPE_KEY_IMAGE_THREE_CARD,

  TYPE_KEY_UNSUPPORTED;

  public static FeedViewType getFeedType(@Nullable Feed feed) {
    if (feed == null) {
      return TYPE_KEY_UNSUPPORTED;
    }

    switch (feed.styleType) {
      case Feed.STYLE_IMAGE_BIG_CARD:
        return TYPE_KEY_IMAGE_BIG_CARD;
      case Feed.STYLE_IMAGE_THREE_CARD:
        return TYPE_KEY_IMAGE_THREE_CARD;
      case Feed.STYLE_PGC_BIG_CARD:
        return TYPE_KEY_PGC_BIG_CARD;
    }

    return TYPE_KEY_UNSUPPORTED;
  }

  /**
   * 卡片工厂
   */
  public static Map<FeedViewType, FeedItemCard> createCardMap() {
    HashMap<FeedViewType, FeedItemCard> cardMap = new HashMap<>();
    cardMap.put(TYPE_KEY_IMAGE_BIG_CARD, new FeedImageBigCard());
    cardMap.put(TYPE_KEY_IMAGE_THREE_CARD, new FeedImageThreePlayCard());
    cardMap.put(TYPE_KEY_PGC_BIG_CARD, new FeedPgcBigCard());
    return cardMap;
  }

}
