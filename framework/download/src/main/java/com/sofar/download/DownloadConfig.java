package com.sofar.download;

import androidx.annotation.NonNull;

import java.io.File;

public class DownloadConfig {

  /**
   * 超时时间，ms
   */
  public int timeout = 30000;

  /**
   * 文件在手机的存储目录
   */
  @NonNull
  public File downloadDir;

  private DownloadConfig() {
  }

  public static class Builder {

    /**
     * 超时时间，ms
     */
    private int timeout = 30000;

    /**
     * 文件在手机的存储目录
     */
    private File downloadDir;

    public Builder setTimeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    public Builder setDownloadDir(@NonNull File downloadDir) {
      this.downloadDir = downloadDir;
      return this;
    }

    public DownloadConfig build() {
      DownloadConfig config = new DownloadConfig();
      config.timeout = timeout;
      config.downloadDir = downloadDir;
      return config;
    }
  }
}
