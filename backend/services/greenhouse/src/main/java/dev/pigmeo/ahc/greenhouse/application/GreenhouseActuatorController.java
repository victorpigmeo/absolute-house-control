package dev.pigmeo.ahc.greenhouse.application;

import dev.pigmeo.ahc.greenhouse.domain.GreenhouseActuatorService;
import dev.pigmeo.ahc.greenhouse.domain.RunPumpCommand;
import dev.pigmeo.ahc.greenhouse.domain.SetFanCommand;
import dev.pigmeo.ahc.greenhouse.domain.SetLedCommand;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greenhouse")
public class GreenhouseActuatorController {

  private final GreenhouseActuatorService greenhouseActuatorService;

  GreenhouseActuatorController(GreenhouseActuatorService greenhouseActuatorService) {
    this.greenhouseActuatorService = greenhouseActuatorService;
  }

  @PostMapping("/led")
  public ActuatorStateResponse setLed(@Valid @RequestBody SetActuatorRequest request) {
    boolean on = greenhouseActuatorService.setLed(new SetLedCommand(request.on()));
    return new ActuatorStateResponse(on);
  }

  @PostMapping("/fan")
  public ActuatorStateResponse setFan(@Valid @RequestBody SetActuatorRequest request) {
    boolean on = greenhouseActuatorService.setFan(new SetFanCommand(request.on()));
    return new ActuatorStateResponse(on);
  }

  @PostMapping("/pump")
  public RunPumpResponse runPump(@Valid @RequestBody RunPumpRequest request) {
    int durationSeconds =
        greenhouseActuatorService.runPump(new RunPumpCommand(request.durationSeconds()));
    return new RunPumpResponse(durationSeconds);
  }
}
