package dev.pigmeo.ahc.greenhouse.domain.exception;

public class InvalidPumpDurationException extends RuntimeException {

  public InvalidPumpDurationException(String message) {
    super(message);
  }
}
