package dev.pigmeo.ahc.greenhouse.infrastructure.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(Esp32Properties.class)
public class Esp32ClientConfig {

  private static final Duration ESP32_TIMEOUT = Duration.ofSeconds(5);

  @Bean
  RestClient esp32RestClient(Esp32Properties properties) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(ESP32_TIMEOUT);
    requestFactory.setReadTimeout(ESP32_TIMEOUT);
    return RestClient.builder()
        .baseUrl(properties.baseUrl())
        .requestFactory(requestFactory)
        .build();
  }
}
