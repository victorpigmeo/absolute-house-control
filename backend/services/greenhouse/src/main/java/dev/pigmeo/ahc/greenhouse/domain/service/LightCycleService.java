package dev.pigmeo.ahc.greenhouse.domain.service;

import dev.pigmeo.ahc.greenhouse.domain.models.CreateLightCycleCommand;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.LightCycle;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.LightCycleRepository;
import org.springframework.stereotype.Service;

@Service
public class LightCycleService {

  private final LightCycleRepository lightCycleRepository;

  LightCycleService(LightCycleRepository lightCycleRepository) {
    this.lightCycleRepository = lightCycleRepository;
  }

  public LightCycle create(CreateLightCycleCommand command) {
    LightCycle lightCycle = new LightCycle(command.name(), command.onCron(), command.offCron());
    return lightCycleRepository.save(lightCycle);
  }
}
