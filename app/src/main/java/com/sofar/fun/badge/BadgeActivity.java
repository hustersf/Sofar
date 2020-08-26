package com.sofar.fun.badge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BadgeActivity extends AppCompatActivity {

  private static final String TAG = "BadgeActivity";

  View friend;
  TextView friendDotTv;

  View video;
  TextView videoDotTv;

  View find;
  TextView findDotTv;

  View comment;
  TextView commentDotTv;

  View like;
  TextView likeDotTv;

  View follow;
  TextView followDotTv;

  View mine;
  TextView mineDotTv;

  View total;
  TextView totalDotTv;

  TextView createDotTv;
  TextView dotInfoTv;


  final int[] types = new int[]{
    BadgeNumber.TYPE_X1,
    BadgeNumber.TYPE_X2,

    BadgeNumber.TYPE_COMMENT,
    BadgeNumber.TYPE_LIKED,
    BadgeNumber.TYPE_FOLLOW,
  };

  Random random = new Random();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("小红点测试");
    setContentView(R.layout.badge_activity);
    initView();
    loadLocalBadge();
  }

  private void initView() {
    friend = findViewById(R.id.friend);
    friendDotTv = findViewById(R.id.friend_dot);
    friend.setOnClickListener(v -> {
      clearBadge(BadgeNumber.TYPE_X1);
    });

    video = findViewById(R.id.video);
    videoDotTv = findViewById(R.id.video_dot);
    video.setOnClickListener(v -> {
      clearBadge(BadgeNumber.TYPE_X2);
    });

    find = findViewById(R.id.find);
    findDotTv = findViewById(R.id.find_dot);

    comment = findViewById(R.id.comment);
    commentDotTv = findViewById(R.id.comment_dot);
    comment.setOnClickListener(v -> {
      clearBadge(BadgeNumber.TYPE_COMMENT);
    });

    like = findViewById(R.id.like);
    likeDotTv = findViewById(R.id.like_dot);
    like.setOnClickListener(v -> {
      clearBadge(BadgeNumber.TYPE_LIKED);
    });

    follow = findViewById(R.id.follow);
    followDotTv = findViewById(R.id.follow_dot);
    follow.setOnClickListener(v -> {
      clearBadge(BadgeNumber.TYPE_FOLLOW);
    });

    mine = findViewById(R.id.mine);
    mineDotTv = findViewById(R.id.mine_dot);

    total = findViewById(R.id.total);
    totalDotTv = findViewById(R.id.total_dot);

    createDotTv = findViewById(R.id.create_dot);
    dotInfoTv = findViewById(R.id.dot_info);
    createDotTv.setOnClickListener(v -> {
      createBadge();
    });
  }


  private void createBadge() {
    BadgeNumber badgeNumber = new BadgeNumber();
    int type = types[random.nextInt(types.length)];//随机一种badge number类型
    badgeNumber.type = type;
    int count = random.nextInt(10) + 1;//count在[1, 10]内随机
    badgeNumber.count = count;
    //父节点显示方式: 视频号是红点,其它都按数字
    int displayMode;
    if ((type == BadgeNumber.TYPE_X2)) {
      displayMode = BadgeNumber.DISPLAY_MODE_ON_PARENT_DOT;
    } else {
      displayMode = BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER;
    }
    badgeNumber.displayMode = displayMode;

    String dotInfo = "生成的红点消息信息:" + badgeName(type) + ":" + count;
    dotInfoTv.setText(dotInfo);

    BadgeNumberTreeManager.get().setBadgeNumber(badgeNumber)
      .subscribe(badge -> {
        updateChildBadge(badge.type, badge.count);
        if (type >= BadgeNumber.TYPE_X1 && type <= BadgeNumber.TYPE_X2) {
          updateFindBadge();
        } else {
          updateMineBadge();
        }
        updateTotalBadge();
      }, throwable -> {
        Log.d(TAG, throwable.toString());
      });
  }

  private void loadLocalBadge() {
    loadBadgeNumber(BadgeNumber.TYPE_X1);
    loadBadgeNumber(BadgeNumber.TYPE_X2);
    loadBadgeNumber(BadgeNumber.TYPE_COMMENT);
    loadBadgeNumber(BadgeNumber.TYPE_LIKED);
    loadBadgeNumber(BadgeNumber.TYPE_FOLLOW);

    updateFindBadge();
    updateMineBadge();
    updateTotalBadge();
  }

  private void loadBadgeNumber(int type) {
    BadgeNumberTreeManager.get().getBadgeNumber(type)
      .subscribe(integer -> {
        updateChildBadge(type, integer);
      }, throwable -> {
        Log.d(TAG, "loadBadgeNumber:" + throwable.toString());
      });
  }

  private void clearBadge(int type) {
    BadgeNumberTreeManager.get().clearBadgeNumber(type).subscribe(aBoolean -> {
      updateChildBadge(type, 0);
      if (type >= BadgeNumber.TYPE_X1 && type <= BadgeNumber.TYPE_X2) {
        updateFindBadge();
      } else {
        updateMineBadge();
      }
      updateTotalBadge();
    }, throwable -> {
      Log.d(TAG, "clearBadge:" + throwable.toString());
    });
  }

  private void updateChildBadge(int type, int count) {
    switch (type) {
      case BadgeNumber.TYPE_X1:
        if (count > 0) {
          friendDotTv.setVisibility(View.VISIBLE);
          friendDotTv.setText(String.valueOf(count));
        } else {
          friendDotTv.setVisibility(View.GONE);
        }
        break;
      case BadgeNumber.TYPE_X2:
        if (count > 0) {
          videoDotTv.setVisibility(View.VISIBLE);
        } else {
          videoDotTv.setVisibility(View.GONE);
        }
        break;
      case BadgeNumber.TYPE_COMMENT:
        if (count > 0) {
          commentDotTv.setVisibility(View.VISIBLE);
          commentDotTv.setText(String.valueOf(count));
        } else {
          commentDotTv.setVisibility(View.GONE);
        }
        break;
      case BadgeNumber.TYPE_LIKED:
        if (count > 0) {
          likeDotTv.setVisibility(View.VISIBLE);
          likeDotTv.setText(String.valueOf(count));
        } else {
          likeDotTv.setVisibility(View.GONE);
        }
        break;
      case BadgeNumber.TYPE_FOLLOW:
        if (count > 0) {
          followDotTv.setVisibility(View.VISIBLE);
          followDotTv.setText(String.valueOf(count));
        } else {
          followDotTv.setVisibility(View.GONE);
        }
        break;
    }
  }

  private void updateFindBadge() {
    List<BadgeNumberInterval> list = new ArrayList<>();
    //发现区间的红点
    BadgeNumberInterval interval = new BadgeNumberInterval();
    interval.typeMin = BadgeNumber.TYPE_X1;
    interval.typeMax = BadgeNumber.TYPE_X2;
    list.add(interval);
    //父亲节点的红点
    BadgeNumberTreeManager.get().getTotalBadgeNumberOnParent(list).subscribe(result -> {
      if (result.totalCount > 0) {
        findDotTv.setVisibility(View.VISIBLE);
        if (result.displayMode == BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER) {
          findDotTv.setText(String.valueOf(result.totalCount));
        } else {
          findDotTv.setText("");
        }
      } else {
        findDotTv.setVisibility(View.GONE);
      }
    });
  }

  private void updateMineBadge() {
    List<BadgeNumberInterval> list = new ArrayList<>();
    //我的区间的红点
    BadgeNumberInterval interval = new BadgeNumberInterval();
    interval.typeMin = BadgeNumber.TYPE_COMMENT;
    interval.typeMax = BadgeNumber.TYPE_FOLLOW;
    list.add(interval);
    //父亲节点的红点
    BadgeNumberTreeManager.get().getTotalBadgeNumberOnParent(list).subscribe(result -> {
      if (result.totalCount > 0) {
        mineDotTv.setVisibility(View.VISIBLE);
        if (result.displayMode == BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER) {
          mineDotTv.setText(String.valueOf(result.totalCount));
        } else {
          mineDotTv.setText("");
        }
      } else {
        mineDotTv.setVisibility(View.GONE);
      }
    });
  }

  private void updateTotalBadge() {
    List<BadgeNumberInterval> list = new ArrayList<>();
    //发现区间的红点
    BadgeNumberInterval interval1 = new BadgeNumberInterval();
    interval1.typeMin = BadgeNumber.TYPE_X1;
    interval1.typeMax = BadgeNumber.TYPE_X2;
    list.add(interval1);
    //我的区间的红点
    BadgeNumberInterval interval2 = new BadgeNumberInterval();
    interval2.typeMin = BadgeNumber.TYPE_COMMENT;
    interval2.typeMax = BadgeNumber.TYPE_FOLLOW;
    list.add(interval2);
    //根节点的红点
    BadgeNumberTreeManager.get().getTotalBadgeNumberOnParent(list).subscribe(result -> {
      if (result.totalCount > 0) {
        totalDotTv.setVisibility(View.VISIBLE);
        if (result.displayMode == BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER) {
          totalDotTv.setText(String.valueOf(result.totalCount));
        } else {
          totalDotTv.setText("");
        }
      } else {
        totalDotTv.setVisibility(View.GONE);
      }
    });
  }

  private String badgeName(int type) {
    switch (type) {
      case BadgeNumber.TYPE_X1:
        return "朋友圈";
      case BadgeNumber.TYPE_X2:
        return "视频号";
      case BadgeNumber.TYPE_COMMENT:
        return "评论";
      case BadgeNumber.TYPE_LIKED:
        return "点赞";
      case BadgeNumber.TYPE_FOLLOW:
        return "关注";
    }
    return "UNKNOWN";
  }


}
