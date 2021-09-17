package com.sofar.fun.play;

import com.sofar.fun.play.singal.VideoControlSignal;
import com.sofar.fun.play.singal.VideoStateSignal;

import io.reactivex.subjects.PublishSubject;

public class VideoContext {

  /**
   * 视频控制信号
   */
  public PublishSubject<VideoControlSignal> mVideoControlPublisher = PublishSubject.create();

  /**
   * 视频状态信号
   */
  public PublishSubject<VideoStateSignal> mVideoStatePublisher = PublishSubject.create();

}
