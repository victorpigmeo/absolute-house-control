package dev.pigmeo.ahc.greenhouse.domain;

public record RunPumpCommand(int durationSeconds) {

  public static final int MAX_DURATION_SECONDS = 3600;

  public RunPumpCommand {
    if (durationSeconds <= 0) {
      throw new InvalidPumpDurationException("durationSeconds must be a positive number");
    }
    if (durationSeconds > MAX_DURATION_SECONDS) {
      throw new InvalidPumpDurationException(
          "durationSeconds must not exceed " + MAX_DURATION_SECONDS);
    }
  }
}
