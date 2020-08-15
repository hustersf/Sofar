package com.sofar.fun.ad.strategy;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sofar.fun.ad.MockAd;
import com.sofar.fun.ad.MockAdInfo;
import com.sofar.fun.ad.task.CountTask;
import com.sofar.fun.ad.task.CountTaskFactory;
import com.sofar.fun.ad.task.MockAdCountTask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class SerialStrategyTest {

  List<MockAdInfo> adInfos = new ArrayList<>();

  @Before
  public void setUp() {
    adInfos.add(new MockAdInfo("111", "AA"));
    adInfos.add(new MockAdInfo("error", "ee"));
    adInfos.add(new MockAdInfo("222", "BB"));
    adInfos.add(new MockAdInfo("333", "CC"));
    adInfos.add(new MockAdInfo("444", "DD"));
  }

  @Test
  public void testAd() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    int count = 4;
    SerialStrategy<MockAdInfo, MockAd> strategy = new SerialStrategy<>();
    strategy.applyStrategy(adInfos, count, new CountTaskFactory<MockAdInfo>() {
      @NonNull
      @Override
      public CountTask createTask(MockAdInfo info) {
        return new MockAdCountTask(info);
      }
    }).subscribe(mockAds -> {
      latch.countDown();
      Assert.assertEquals(mockAds.size(), count);
    });

    latch.await();
  }

}
