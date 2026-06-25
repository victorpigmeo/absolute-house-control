package dev.pigmeo.ahc.greenhouse.application.dto;

import jakarta.validation.constraints.NotNull;

public record SetActuatorRequest(@NotNull Boolean on) {}
