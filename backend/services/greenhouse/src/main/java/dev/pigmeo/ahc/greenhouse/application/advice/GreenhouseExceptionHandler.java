package dev.pigmeo.ahc.greenhouse.application.advice;

import dev.pigmeo.ahc.greenhouse.application.dto.ErrorResponse;
import dev.pigmeo.ahc.greenhouse.domain.exception.InvalidPumpDurationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GreenhouseExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
    List<String> details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .toList();
    return new ErrorResponse("Validation failed", details);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMalformedJson(HttpMessageNotReadableException ex) {
    return new ErrorResponse("Malformed request body", List.of());
  }

  @ExceptionHandler(InvalidPumpDurationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidPumpDuration(InvalidPumpDurationException ex) {
    return new ErrorResponse(ex.getMessage(), List.of());
  }
}
