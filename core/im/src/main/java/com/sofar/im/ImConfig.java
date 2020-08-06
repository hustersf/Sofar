package com.sofar.im;

/**
 * IM配置
 */
public class ImConfig {

  String appId;
  String appName;

  public ImConfig(Builder builder) {
    this.appId = builder.appId;
    this.appName = builder.appName;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static final class Builder {
    String appId;
    String appName;

    public Builder() {
    }

    public Builder appId(String appId) {
      this.appId = appId;
      return this;
    }

    public Builder appName(String appName) {
      this.appName = appName;
      return this;
    }

    Builder(ImConfig config) {
      this.appId = config.appId;
      this.appName = config.appName;
    }

    public ImConfig build() {
      return new ImConfig(this);
    }
  }

}
