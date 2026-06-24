package dev.pigmeo.ahc.greenhouse.domain;

public class InvalidPumpDurationException extends RuntimeException {

  public InvalidPumpDurationException(String message) {
    super(message);
  }
}
