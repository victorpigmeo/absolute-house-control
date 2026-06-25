package dev.pigmeo.ahc.greenhouse.domain.service;

import dev.pigmeo.ahc.greenhouse.domain.models.RunPumpCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetFanCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetLedCommand;
import dev.pigmeo.ahc.greenhouse.infrastructure.client.Esp32GpioClient;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class GreenhouseActuatorService {

  private final Esp32GpioClient esp32GpioClient;
  private final TaskScheduler taskScheduler;
  private final AtomicReference<ScheduledFuture<?>> pendingPumpOff = new AtomicReference<>();

  GreenhouseActuatorService(Esp32GpioClient esp32GpioClient, TaskScheduler taskScheduler) {
    this.esp32GpioClient = esp32GpioClient;
    this.taskScheduler = taskScheduler;
  }

  public boolean setLed(SetLedCommand command) {
    return setActuator(esp32GpioClient::setLed, command.on());
  }

  public boolean setFan(SetFanCommand command) {
    return setActuator(esp32GpioClient::setFan, command.on());
  }

  public int runPump(RunPumpCommand command) {
    Instant offAt = Instant.now().plusSeconds(command.durationSeconds());
    cancelPendingPumpOff();
    esp32GpioClient.setPump(true);
    pendingPumpOff.set(taskScheduler.schedule(() -> esp32GpioClient.setPump(false), offAt));
    return command.durationSeconds();
  }

  private boolean setActuator(Consumer<Boolean> esp32Setter, boolean on) {
    esp32Setter.accept(on);
    return on;
  }

  private void cancelPendingPumpOff() {
    ScheduledFuture<?> previous = pendingPumpOff.getAndSet(null);
    if (previous != null) {
      previous.cancel(false);
    }
  }
}
