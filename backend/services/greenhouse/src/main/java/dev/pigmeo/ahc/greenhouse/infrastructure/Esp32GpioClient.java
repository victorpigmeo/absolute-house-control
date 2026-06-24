package dev.pigmeo.ahc.greenhouse.infrastructure;

import dev.pigmeo.ahc.greenhouse.infrastructure.config.Esp32Properties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Talks to the greenhouse ESP32's GPIO endpoint. The board is active-low and fire-and-forget: a
 * command is the only signal of state there is (no read-back exists in the v1 firmware).
 */
@Component
public class Esp32GpioClient {

  private static final int GPIO_ON = 0;
  private static final int GPIO_OFF = 1;

  private final RestClient esp32RestClient;
  private final Esp32Properties properties;

  Esp32GpioClient(RestClient esp32RestClient, Esp32Properties properties) {
    this.esp32RestClient = esp32RestClient;
    this.properties = properties;
  }

  public void setLed(boolean on) {
    setGpio(properties.pins().led(), on);
  }

  public void setFan(boolean on) {
    setGpio(properties.pins().fan(), on);
  }

  public void setPump(boolean on) {
    setGpio(properties.pins().pump(), on);
  }

  private void setGpio(int pin, boolean on) {
    int state = on ? GPIO_ON : GPIO_OFF;
    esp32RestClient
        .get()
        .uri("/api/gpio/set/{pin}/{state}", pin, state)
        .retrieve()
        .toBodilessEntity();
  }
}
