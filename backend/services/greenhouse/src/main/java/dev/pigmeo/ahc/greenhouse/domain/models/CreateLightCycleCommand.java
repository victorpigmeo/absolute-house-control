package dev.pigmeo.ahc.greenhouse.domain.models;

public record CreateLightCycleCommand(String name, String onCron, String offCron) {}
