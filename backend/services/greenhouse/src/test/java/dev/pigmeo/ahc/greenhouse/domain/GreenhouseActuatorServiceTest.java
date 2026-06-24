package dev.pigmeo.ahc.greenhouse.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.pigmeo.ahc.greenhouse.infrastructure.Esp32GpioClient;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

@ExtendWith(MockitoExtension.class)
class GreenhouseActuatorServiceTest {

  @Mock private Esp32GpioClient esp32GpioClient;
  @Mock private TaskScheduler taskScheduler;

  @Test
  void setLed_on_callsClientWithTrueAndReturnsOn() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);

    boolean result = service.setLed(new SetLedCommand(true));

    verify(esp32GpioClient).setLed(true);
    assertThat(result).isTrue();
  }

  @Test
  void setLed_off_callsClientWithFalseAndReturnsOff() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);

    boolean result = service.setLed(new SetLedCommand(false));

    verify(esp32GpioClient).setLed(false);
    assertThat(result).isFalse();
  }

  @Test
  void setFan_on_callsClientWithTrueAndReturnsOn() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);

    boolean result = service.setFan(new SetFanCommand(true));

    verify(esp32GpioClient).setFan(true);
    assertThat(result).isTrue();
  }

  @Test
  void setFan_off_callsClientWithFalseAndReturnsOff() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);

    boolean result = service.setFan(new SetFanCommand(false));

    verify(esp32GpioClient).setFan(false);
    assertThat(result).isFalse();
  }

  @Test
  void runPump_sendsOnImmediatelyAndSchedulesOffAtCorrectDelay() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);
    Instant before = Instant.now();

    int result = service.runPump(new RunPumpCommand(5));

    verify(esp32GpioClient).setPump(true);
    ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(taskScheduler).schedule(any(Runnable.class), instantCaptor.capture());
    Instant scheduledAt = instantCaptor.getValue();
    assertThat(scheduledAt).isAfterOrEqualTo(before.plusSeconds(5).minusSeconds(2));
    assertThat(scheduledAt).isBeforeOrEqualTo(Instant.now().plusSeconds(5).plusSeconds(2));
    assertThat(result).isEqualTo(5);
  }

  @Test
  void runPump_whenScheduledTaskRuns_sendsOff() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);

    service.runPump(new RunPumpCommand(5));

    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(taskScheduler).schedule(runnableCaptor.capture(), any(Instant.class));
    verify(esp32GpioClient, never()).setPump(false);

    runnableCaptor.getValue().run();

    verify(esp32GpioClient).setPump(false);
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void runPump_calledAgainBeforeFirstOffFires_cancelsPreviousScheduledOff() {
    GreenhouseActuatorService service =
        new GreenhouseActuatorService(esp32GpioClient, taskScheduler);
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
