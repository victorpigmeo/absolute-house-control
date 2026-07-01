package dev.pigmeo.ahc.greenhouse.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a string to be a valid 6-field cron expression (second minute hour day-of-month month
 * day-of-week), as accepted by Spring's {@link
 * org.springframework.scheduling.support.CronExpression}. Blank/null values are considered valid
 * here; pair with {@code @NotBlank} to also require a value.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CronSyntaxValidator.class)
public @interface ValidCron {

  String message() default "must be a valid 6-field cron expression";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
