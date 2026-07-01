package dev.pigmeo.ahc.greenhouse.application.controllers;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.pigmeo.ahc.greenhouse.domain.models.RunPumpCommand;
import dev.pigmeo.ahc.greenhouse.domain.service.GreenhouseActuatorService;
import dev.pigmeo.ahc.greenhouse.infrastructure.client.Esp32GpioClient;
import dev.pigmeo.ahc.greenhouse.infrastructure.config.Esp32ClientConfig;
import dev.pigmeo.ahc.greenhouse.infrastructure.config.PumpSchedulerConfig;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.ActuatorStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreenhouseActuatorController.class)
@Import({
  Esp32ClientConfig.class,
  PumpSchedulerConfig.class,
  Esp32GpioClient.class,
  GreenhouseActuatorService.class
})
class GreenhouseActuatorControllerTest {

  @RegisterExtension static WireMockExtension wireMock = WireMockExtension.newInstance().build();

  @DynamicPropertySource
  static void esp32Properties(DynamicPropertyRegistry registry) {
    registry.add("greenhouse.esp32.base-url", wireMock::baseUrl);
  }

  @MockitoBean private ActuatorStateRepository actuatorStateRepository;

  @Autowired private MockMvc mockMvc;

  private static void stubGpio(int pin, int state) {
    wireMock.stubFor(
        get(urlEqualTo("/api/gpio/set/" + pin + "/" + state))
            .willReturn(okJson("{\"pin\":" + pin + ",\"value\":" + state + "}")));
  }

  @Test
  void setLed_on_callsEsp32AndReturnsOn() throws Exception {
    stubGpio(32, 0);

    mockMvc
        .perform(
            post("/api/greenhouse/led")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"on\": true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.on").value(true));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/32/0")));
  }

  @Test
  void setLed_off_callsEsp32WithState1AndReturnsOff() throws Exception {
    stubGpio(32, 1);

    mockMvc
        .perform(
            post("/api/greenhouse/led")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"on\": false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.on").value(false));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/32/1")));
  }

  @Test
  void setFan_on_callsEsp32Pin26State0() throws Exception {
    stubGpio(26, 0);

    mockMvc
        .perform(
            post("/api/greenhouse/fan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"on\": true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.on").value(true));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/26/0")));
  }

  @Test
  void setFan_off_callsEsp32Pin26State1() throws Exception {
    stubGpio(26, 1);

    mockMvc
        .perform(
            post("/api/greenhouse/fan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"on\": false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.on").value(false));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/26/1")));
  }

  @Test
  void runPump_withValidDuration_sendsOnImmediatelyAndReturns200() throws Exception {
    stubGpio(25, 0);
    stubGpio(25, 1);

    mockMvc
        .perform(
            post("/api/greenhouse/pump")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"durationSeconds\": 5}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.durationSeconds").value(5));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/25/0")));
    wireMock.verify(0, getRequestedFor(urlEqualTo("/api/gpio/set/25/1")));
  }

  @Test
  void runPump_missingDuration_returns400AndDoesNotCallEsp32() throws Exception {
    mockMvc
        .perform(post("/api/greenhouse/pump").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"));

    wireMock.verify(0, getRequestedFor(urlMatching("/api/gpio/set/25/.*")));
  }

  @Test
  void runPump_nonNumericDuration_returns400AndDoesNotCallEsp32() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/pump")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"durationSeconds\": \"thirty\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Malformed request body"));

    wireMock.verify(0, getRequestedFor(urlMatching("/api/gpio/set/25/.*")));
  }

  @Test
  void runPump_zeroDuration_returns400AndDoesNotCallEsp32() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/pump")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"durationSeconds\": 0}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"));

    wireMock.verify(0, getRequestedFor(urlMatching("/api/gpio/set/25/.*")));
  }

  @Test
  void runPump_negativeDuration_returns400AndDoesNotCallEsp32() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/pump")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"durationSeconds\": -5}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"));

    wireMock.verify(0, getRequestedFor(urlMatching("/api/gpio/set/25/.*")));
  }

  @Test
  void runPump_durationExceedsMaximum_returns400AndDoesNotCallEsp32() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/pump")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"durationSeconds\": " + (RunPumpCommand.MAX_DURATION_SECONDS + 1) + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"));

    wireMock.verify(0, getRequestedFor(urlMatching("/api/gpio/set/25/.*")));
  }
}
