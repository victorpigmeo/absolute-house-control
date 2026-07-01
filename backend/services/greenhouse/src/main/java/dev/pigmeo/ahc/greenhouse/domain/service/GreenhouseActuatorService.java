package dev.pigmeo.ahc.greenhouse.domain.service;

import dev.pigmeo.ahc.greenhouse.domain.models.GreenhouseState;
import dev.pigmeo.ahc.greenhouse.domain.models.RunPumpCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetFanCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetLedCommand;
import dev.pigmeo.ahc.greenhouse.infrastructure.client.Esp32GpioClient;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.ActuatorState;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.ActuatorStateRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class GreenhouseActuatorService {

  private static final String DEVICE_LED = "led";
  private static final String DEVICE_FAN = "fan";

  private final Esp32GpioClient esp32GpioClient;
  private final TaskScheduler taskScheduler;
  private final ActuatorStateRepository actuatorStateRepository;
  private final AtomicReference<ScheduledFuture<?>> pendingPumpOff = new AtomicReference<>();

  GreenhouseActuatorService(
      Esp32GpioClient esp32GpioClient,
      TaskScheduler taskScheduler,
      ActuatorStateRepository actuatorStateRepository) {
    this.esp32GpioClient = esp32GpioClient;
    this.taskScheduler = taskScheduler;
    this.actuatorStateRepository = actuatorStateRepository;
  }

  public boolean setLed(SetLedCommand command) {
    return setActuator(DEVICE_LED, esp32GpioClient::setLed, command.on());
  }

  public boolean setFan(SetFanCommand command) {
    return setActuator(DEVICE_FAN, esp32GpioClient::setFan, command.on());
  }

  public GreenhouseState getState() {
    Map<String, Boolean> onByDevice =
        actuatorStateRepository.findByDeviceIn(List.of(DEVICE_LED, DEVICE_FAN)).stream()
            .collect(Collectors.toMap(ActuatorState::getDevice, ActuatorState::isOn));
    return new GreenhouseState(
        onByDevice.getOrDefault(DEVICE_LED, false), onByDevice.getOrDefault(DEVICE_FAN, false));
  }

  public int runPump(RunPumpCommand command) {
    Instant offAt = Instant.now().plusSeconds(command.durationSeconds());
    cancelPendingPumpOff();
    esp32GpioClient.setPump(true);
    pendingPumpOff.set(taskScheduler.schedule(() -> esp32GpioClient.setPump(false), offAt));
    return command.durationSeconds();
  }

  private boolean setActuator(String device, Consumer<Boolean> esp32Setter, boolean on) {
    actuatorStateRepository.saveState(device, on);
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
