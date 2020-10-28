package com.sofar.download;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参照OkHttp的Dispatcher
 */
public class DownloadDispatcher {

  private int maxRequests = 10;

  /**
   * generating monotonically-increasing sequence numbers for requests.
   */
  private AtomicInteger increase = new AtomicInteger();

  /**
   * Execute requests
   */
  @NonNull
  private ExecutorService executorService;

  /**
   * Ready download request in the order they'll be run.
   */
  private final Deque<DownloadRequest> readyRequests = new ArrayDeque<>();

  /**
   * Running download request Includes canceled request that haven't finished yet.
   */
  private final Deque<DownloadRequest> runningRequests = new ArrayDeque<>();

  private static class Inner {
    private static DownloadDispatcher INSTANCE = new DownloadDispatcher();
  }

  private DownloadDispatcher() {
    executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>(), Util.threadFactory("Download Dispatcher", false));
  }

  public static DownloadDispatcher get() {
    return DownloadDispatcher.Inner.INSTANCE;
  }


  public synchronized int enqueue(@NonNull DownloadRequest request) {
    if (runningRequests.contains(request) || readyRequests.contains(request)) {
      Log.d(DownloadManager.TAG,"request has already in download queue");
      return request.downloadId;
    }

    int downloadId = getDownloadId();
    request.setDownloadId(downloadId);
    if (runningRequests.size() < maxRequests) {
      runningRequests.add(request);
      executorService.execute(new DownloadTask(request));
    } else {
      readyRequests.add(request);
      request.waiting();
    }
    return downloadId;
  }

  public synchronized void cancel(int downloadId) {
    for (DownloadRequest request : runningRequests) {
      if (downloadId == request.downloadId) {
        request.cancel();
        return;
      }
    }

    for (DownloadRequest request : readyRequests) {
      if (downloadId == request.downloadId) {
        request.cancel();
        return;
      }
    }
  }

  public synchronized void cancelAll() {
    for (DownloadRequest request : runningRequests) {
      request.cancel();
    }

    for (DownloadRequest request : readyRequests) {
      request.cancel();
    }
  }

  @DownloadStatus
  public int query(int downloadId) {
    for (DownloadRequest request : runningRequests) {
      if (downloadId == request.downloadId) {
        return request.getStatus();
      }
    }

    return DownloadStatus.INIT;
  }

  public synchronized void finished(@NonNull DownloadRequest request) {
    if (runningRequests.remove(request)) {
      promoteRequests();
    }
  }

  private void promoteRequests() {
    if (runningRequests.size() >= maxRequests) return; // Already running max capacity.
    if (readyRequests.isEmpty()) return; // No ready calls to promote.

    for (Iterator<DownloadRequest> i = readyRequests.iterator(); i.hasNext(); ) {
      DownloadRequest request = i.next();

      i.remove();
      runningRequests.add(request);
      executorService.execute(new DownloadTask(request));

      if (runningRequests.size() >= maxRequests) return; // Reached max capacity.
    }
  }

  private int getDownloadId() {
    return increase.incrementAndGet();
  }
}
