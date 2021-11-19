package com.sofar.fun.play;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.fun.play.card.FeedItemCard;
import com.sofar.fun.play.core.FeedPlayer;
import com.sofar.fun.play.core.ItemScroll;
import com.sofar.fun.play.core.RecyclerViewPlayer;
import com.sofar.widget.DataProvider;

import java.util.Map;

public class AutoPlayListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("自动播放列表(比例大的优先)");
    setContentView(R.layout.fun_auto_play_activity);

    initList();
  }

  private void initList() {
    RecyclerView playRecycler = findViewById(R.id.play_recycler);
    playRecycler.setLayoutManager(new LinearLayoutManager(this));

    FeedPlayer feedPlayer = FeedPlayer.create();
    RecyclerViewPlayer player = new RecyclerViewPlayer(feedPlayer);
    player.attachToRecyclerView(playRecycler);
    player.setItemScroll(ItemScroll.TOP);

    Map<FeedViewType, FeedItemCard> cardMap = FeedViewType.createCardMap();
    FeedItemInject feedItemInject = new FeedItemInject(cardMap);
    feedItemInject.setFeedPlayer(feedPlayer);
    feedItemInject.setRecyclerViewPlayer(player);
    feedItemInject.bind();

    AutoPlayAdapter adapter = new AutoPlayAdapter(cardMap);
    adapter.setList(DataProvider.feeds());
    playRecycler.setAdapter(adapter);

    playRecycler.post(() -> player.recapture());

  }
}
