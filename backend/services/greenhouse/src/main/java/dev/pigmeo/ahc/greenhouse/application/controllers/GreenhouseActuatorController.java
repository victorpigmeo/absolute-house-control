package dev.pigmeo.ahc.greenhouse.application.controllers;

import dev.pigmeo.ahc.greenhouse.application.dto.ActuatorStateResponse;
import dev.pigmeo.ahc.greenhouse.application.dto.GreenhouseStateResponse;
import dev.pigmeo.ahc.greenhouse.application.dto.RunPumpRequest;
import dev.pigmeo.ahc.greenhouse.application.dto.RunPumpResponse;
import dev.pigmeo.ahc.greenhouse.application.dto.SetActuatorRequest;
import dev.pigmeo.ahc.greenhouse.domain.models.GreenhouseState;
import dev.pigmeo.ahc.greenhouse.domain.models.RunPumpCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetFanCommand;
import dev.pigmeo.ahc.greenhouse.domain.models.SetLedCommand;
import dev.pigmeo.ahc.greenhouse.domain.service.GreenhouseActuatorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/state")
  public GreenhouseStateResponse getState() {
    GreenhouseState state = greenhouseActuatorService.getState();
    return new GreenhouseStateResponse(state.led(), state.fan());
  }
}
