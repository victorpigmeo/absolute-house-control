package dev.pigmeo.ahc.greenhouse.application;

import jakarta.validation.constraints.NotNull;

public record SetActuatorRequest(@NotNull Boolean on) {}
