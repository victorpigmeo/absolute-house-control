package dev.pigmeo.ahc.greenhouse.application.dto;

import dev.pigmeo.ahc.greenhouse.application.validation.ValidCron;
import jakarta.validation.constraints.NotBlank;

public record CreateLightCycleRequest(
    @NotBlank String name,
    @NotBlank @ValidCron String onCron,
    @NotBlank @ValidCron String offCron) {}
