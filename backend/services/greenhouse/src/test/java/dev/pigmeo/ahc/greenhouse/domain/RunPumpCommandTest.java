package dev.pigmeo.ahc.greenhouse.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RunPumpCommandTest {

  @Test
  void constructor_withPositiveDuration_succeeds() {
    RunPumpCommand command = new RunPumpCommand(30);

    assertThat(command.durationSeconds()).isEqualTo(30);
  }

  @Test
  void constructor_withZeroDuration_throwsInvalidPumpDurationException() {
    assertThatThrownBy(() -> new RunPumpCommand(0))
        .isInstanceOf(InvalidPumpDurationException.class);
  }

  @Test
  void constructor_withNegativeDuration_throwsInvalidPumpDurationException() {
    assertThatThrownBy(() -> new RunPumpCommand(-5))
        .isInstanceOf(InvalidPumpDurationException.class);
  }

  @Test
  void constructor_withDurationAtMaximum_succeeds() {
    RunPumpCommand command = new RunPumpCommand(RunPumpCommand.MAX_DURATION_SECONDS);

    assertThat(command.durationSeconds()).isEqualTo(RunPumpCommand.MAX_DURATION_SECONDS);
  }

  @Test
  void constructor_withDurationAboveMaximum_throwsInvalidPumpDurationException() {
    assertThatThrownBy(() -> new RunPumpCommand(RunPumpCommand.MAX_DURATION_SECONDS + 1))
        .isInstanceOf(InvalidPumpDurationException.class);
  }
}
