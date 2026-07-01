package dev.pigmeo.ahc.greenhouse.domain.service;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.pigmeo.ahc.greenhouse.domain.models.RunPumpCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetFanCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetLedCommand;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.ActuatorState;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.ActuatorStateRepository;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
class GreenhouseActuatorServiceTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:17").withDatabaseName("house_control");

  @RegisterExtension static WireMockExtension wireMock = WireMockExtension.newInstance().build();

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("greenhouse.esp32.base-url", wireMock::baseUrl);
  }

  @MockitoBean private TaskScheduler taskScheduler;

  @Autowired private GreenhouseActuatorService service;
  @Autowired private ActuatorStateRepository repository;

  private static void stubGpio(int pin, int state) {
    wireMock.stubFor(
        get(urlEqualTo("/api/gpio/set/" + pin + "/" + state))
            .willReturn(okJson("{\"pin\":" + pin + ",\"value\":" + state + "}")));
  }

  @Test
  void setLed_on_persistsNewRecordAsOn() {
    stubGpio(32, 0);

    boolean result = service.setLed(new SetLedCommand(true));

    assertThat(result).isTrue();
    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/32/0")));
    ActuatorState state = repository.findByDevice("led").orElseThrow();
    assertThat(state.isOn()).isTrue();
    assertThat(state.getCreatedAt()).isNotNull();
    assertThat(state.getUpdatedAt()).isNull();
    assertThat(state.getVersion()).isEqualTo(1L);
  }

  @Test
  void setLed_off_persistsNewRecordAsOff() {
    stubGpio(32, 1);

    boolean result = service.setLed(new SetLedCommand(false));

    assertThat(result).isFalse();
    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/32/1")));
    assertThat(repository.isOn("led")).isFalse();
  }

  @Test
  void setFan_on_persistsNewRecordAsOn() {
    stubGpio(26, 0);

    boolean result = service.setFan(new SetFanCommand(true));

    assertThat(result).isTrue();
    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/26/0")));
    assertThat(repository.isOn("fan")).isTrue();
  }

  @Test
  void setFan_off_persistsNewRecordAsOff() {
    stubGpio(26, 1);

    boolean result = service.setFan(new SetFanCommand(false));

    assertThat(result).isFalse();
    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/26/1")));
    assertThat(repository.isOn("fan")).isFalse();
  }

  @Test
  @Sql(
      statements =
          "insert into greenhouse.actuator_state (device, is_on, created_at, version) "
              + "values ('fan', true, now(), 1)")
  void setFan_off_whenPreviouslyOn_updatesExistingRecordInPlace() {
    stubGpio(26, 1);

    service.setFan(new SetFanCommand(false));

    ActuatorState state = repository.findByDevice("fan").orElseThrow();
    assertThat(state.isOn()).isFalse();
    assertThat(state.getUpdatedAt()).isNotNull();
    assertThat(state.getVersion()).isEqualTo(2L);
  }

  @Test
  void runPump_sendsOnImmediatelyAndSchedulesOffAtCorrectDelay() {
    stubGpio(25, 0);
    Instant before = Instant.now();

    int result = service.runPump(new RunPumpCommand(5));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/25/0")));
    ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(taskScheduler).schedule(any(Runnable.class), instantCaptor.capture());
    Instant scheduledAt = instantCaptor.getValue();
    assertThat(scheduledAt).isAfterOrEqualTo(before.plusSeconds(5).minusSeconds(2));
    assertThat(scheduledAt).isBeforeOrEqualTo(Instant.now().plusSeconds(5).plusSeconds(2));
    assertThat(result).isEqualTo(5);
  }

  @Test
  void runPump_whenScheduledTaskRuns_sendsOff() {
    stubGpio(25, 0);
    stubGpio(25, 1);

    service.runPump(new RunPumpCommand(5));

    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(taskScheduler).schedule(runnableCaptor.capture(), any(Instant.class));
    wireMock.verify(0, getRequestedFor(urlEqualTo("/api/gpio/set/25/1")));

    runnableCaptor.getValue().run();

    wireMock.verify(getRequestedFor(urlEqualTo("/api/gpio/set/25/1")));
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void runPump_calledAgainBeforeFirstOffFires_cancelsPreviousScheduledOff() {
    stubGpio(25, 0);
    ScheduledFuture firstFuture = mock(ScheduledFuture.class);
    ScheduledFuture secondFuture = mock(ScheduledFuture.class);
    when(taskScheduler.schedule(any(Runnable.class), any(Instant.class)))
        .thenReturn(firstFuture, secondFuture);

    service.runPump(new RunPumpCommand(5));
    service.runPump(new RunPumpCommand(7));

    verify(firstFuture).cancel(false);
    verify(secondFuture, never()).cancel(anyBoolean());
  }
}
