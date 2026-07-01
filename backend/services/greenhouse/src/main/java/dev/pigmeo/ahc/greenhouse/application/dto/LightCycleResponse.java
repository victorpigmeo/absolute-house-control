package dev.pigmeo.ahc.greenhouse.application.dto;

public record LightCycleResponse(
    Long id, String name, String onCron, String offCron, boolean active) {}
