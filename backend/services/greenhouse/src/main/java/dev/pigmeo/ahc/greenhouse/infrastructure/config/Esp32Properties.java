package dev.pigmeo.ahc.greenhouse.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "greenhouse.esp32")
public record Esp32Properties(String baseUrl, Pins pins) {

  public record Pins(int led, int fan, int pump) {}
}
