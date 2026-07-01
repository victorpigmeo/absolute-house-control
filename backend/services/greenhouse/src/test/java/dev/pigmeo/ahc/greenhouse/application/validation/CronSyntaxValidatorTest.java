package dev.pigmeo.ahc.greenhouse.application.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CronSyntaxValidatorTest {

  // The validator never touches its context argument, so tests pass null rather than a mock.
  private final CronSyntaxValidator validator = new CronSyntaxValidator();

  @Test
  void isValid_withNull_returnsTrue() {
    assertThat(validator.isValid(null, null)).isTrue();
  }

  @Test
  void isValid_withBlank_returnsTrue() {
    assertThat(validator.isValid("   ", null)).isTrue();
  }

  @Test
  void isValid_withValidSixFieldExpression_returnsTrue() {
    assertThat(validator.isValid("0 0 8 * * *", null)).isTrue();
  }

  @Test
  void isValid_withFiveFieldExpression_returnsFalse() {
    assertThat(validator.isValid("0 8 * * *", null)).isFalse();
  }

  @Test
  void isValid_withGarbage_returnsFalse() {
    assertThat(validator.isValid("not a cron expression", null)).isFalse();
  }
}
