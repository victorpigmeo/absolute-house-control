package dev.pigmeo.ahc.greenhouse.application.dto;

import java.util.List;

public record ErrorResponse(String error, List<String> details) {}
