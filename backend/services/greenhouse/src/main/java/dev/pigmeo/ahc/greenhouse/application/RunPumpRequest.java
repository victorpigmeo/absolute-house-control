package dev.pigmeo.ahc.greenhouse.application;

import dev.pigmeo.ahc.greenhouse.domain.RunPumpCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RunPumpRequest(
    @NotNull @Positive @Max(RunPumpCommand.MAX_DURATION_SECONDS) Integer durationSeconds) {}
