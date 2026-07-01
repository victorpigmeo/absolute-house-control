package dev.pigmeo.ahc.greenhouse.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.scheduling.support.CronExpression;

/** Delegates to Spring's own 6-field cron parser rather than reimplementing cron syntax. */
class CronSyntaxValidator implements ConstraintValidator<ValidCron, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || value.isBlank() || CronExpression.isValidExpression(value);
  }
}
