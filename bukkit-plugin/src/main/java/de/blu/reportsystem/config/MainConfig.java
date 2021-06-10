package de.blu.reportsystem.config;

import lombok.Getter;

import javax.inject.Singleton;

@Singleton
public final class MainConfig {

  @Getter private String serviceUrl = "http://localhost:8080";

  public void init() {
    // Load Config
  }
}
