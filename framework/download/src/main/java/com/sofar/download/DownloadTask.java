package com.sofar.download;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class DownloadTask implements Runnable {

  @NonNull
  DownloadRequest request;

  @NonNull
  DownloadConfig config;

  public DownloadTask(@NonNull DownloadRequest request) {
    this.request = request;
    config = DownloadManager.get().getConfig();
  }

  @Override
  public void run() {
    if (request.isCanceled()) {
      return;
    }
    executeDownload(request.uri.toString());
  }

  private void executeDownload(String downloadUrl) {
    HttpURLConnection conn = null;
    try {
      URL url = new URL(downloadUrl);
      conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(config.timeout);
      conn.setReadTimeout(config.timeout);

      for (Map.Entry<String, String> entry : request.headers.entrySet()) {
        conn.addRequestProperty(entry.getKey(), entry.getValue());
      }
      conn.setRequestProperty("Accept-Encoding", "identity");
      downloadConnected();

      long contentLength = getContentLengthLong(conn);
      int responseCode = conn.getResponseCode();

      StringBuffer sb = new StringBuffer();
      sb.append("http response code=" + responseCode);
      sb.append(" contentLength=" + contentLength);
      sb.append(" from downloadId=" + request.downloadId);
      Log.d(DownloadManager.TAG, sb.toString());

      if (contentLength == -1) {
        downloadFailed(-1, "error contentLength=-1");
        return;
      }

      if (responseCode == HttpURLConnection.HTTP_OK) {
        saveData(conn, contentLength);
      } else {
        downloadFailed(responseCode, "response code error");
      }
    } catch (Exception e) {
      e.printStackTrace();
      downloadFailed(-1, e.toString());
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  private void saveData(HttpURLConnection conn, long contentLength) {
    if (contentLength <= 0) {
      return;
    }

    InputStream is = null;
    OutputStream ous = null;
    try {
      is = conn.getInputStream();
      File dirFile = new File(request.fileDirPath);
      if (!dirFile.exists()) {
        dirFile.mkdirs();
      }
      File targetFile = new File(dirFile, request.fileName);
      if (!targetFile.exists()) {
        targetFile.createNewFile();
      }
      ous = new FileOutputStream(targetFile);

      byte[] buffer = new byte[1024];
      int length = -1;
      long downloadedBytes = 0;
      while ((length = is.read(buffer)) != -1) {
        ous.write(buffer, 0, length);
        downloadedBytes += length;
        downloadProgress(contentLength, downloadedBytes);

        if (request.isCanceled()) {
          return;
        }
      }

      downloadComplete();
    } catch (IOException e) {
      e.printStackTrace();
      downloadFailed(-1, e.toString());
    } finally {
      Util.closeQuietly(is);
      Util.closeQuietly(ous);
    }

  }

  private void downloadConnected() {
    request.connect();
  }

  private void downloadFailed(int code, String message) {
    request.error(code, message);
  }

  private void downloadProgress(long totalBytes, long downloadedBytes) {
    int progress = totalBytes > 0 ? (int) (1.0f * 100 * downloadedBytes / totalBytes) : 0;
    request.progress(totalBytes, downloadedBytes, progress);
  }

  private void downloadComplete() {
    request.complete();
  }

  private long getContentLengthLong(HttpURLConnection conn) {
    return getHeaderFieldLong(conn, "content-length", -1);
  }

  private long getHeaderFieldLong(URLConnection conn, String field, long defaultValue) {
    try {
      return Long.parseLong(conn.getHeaderField(field));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
