package dev.pigmeo.ahc.greenhouse.application.controllers;

import dev.pigmeo.ahc.greenhouse.application.dto.CreateLightCycleRequest;
import dev.pigmeo.ahc.greenhouse.application.dto.LightCycleResponse;
import dev.pigmeo.ahc.greenhouse.domain.models.CreateLightCycleCommand;
import dev.pigmeo.ahc.greenhouse.domain.service.LightCycleService;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.LightCycle;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greenhouse")
public class LightCycleController {

  private final LightCycleService lightCycleService;

  LightCycleController(LightCycleService lightCycleService) {
    this.lightCycleService = lightCycleService;
  }

  @PostMapping("/light-cycles")
  public LightCycleResponse create(@Valid @RequestBody CreateLightCycleRequest request) {
    LightCycle lightCycle =
        lightCycleService.create(
            new CreateLightCycleCommand(request.name(), request.onCron(), request.offCron()));
    return new LightCycleResponse(
        lightCycle.getId(),
        lightCycle.getName(),
        lightCycle.getOnCron(),
        lightCycle.getOffCron(),
        lightCycle.isActive());
  }
}
