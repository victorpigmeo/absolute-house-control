package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class ActuatorStateRepositoryIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:17").withDatabaseName("house_control");

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private ActuatorStateRepository repository;

  @Test
  void unsetDevice_defaultsToOff() {
    assertThat(repository.isOn("never-configured")).isFalse();
  }

  @Test
  void saveState_insertsNewRecord_setsCreatedAtAndLeavesUpdatedAtNull() {
    repository.saveState("insert-check", true);

    ActuatorState state = repository.findByDevice("insert-check").orElseThrow();
    assertThat(state.isOn()).isTrue();
    assertThat(state.getCreatedAt()).isNotNull();
    assertThat(state.getUpdatedAt()).isNull();
    assertThat(state.getVersion()).isEqualTo(1L);
  }

  @Test
  void saveState_calledAgainForSameDevice_updatesInPlace() {
    repository.saveState("update-check", true);

    repository.saveState("update-check", false);

    ActuatorState state = repository.findByDevice("update-check").orElseThrow();
    assertThat(state.isOn()).isFalse();
    assertThat(state.getUpdatedAt()).isNotNull();
    assertThat(state.getVersion()).isEqualTo(2L);
  }

  @Test
  void saveState_persistsAcrossSimulatedRestart() throws Exception {
    repository.saveState("restart-check", true);

    // Reads back via a brand-new JDBC connection, bypassing Hibernate/the app's persistence
    // context entirely, to prove the state lives in the database table rather than in any
    // in-process object -- i.e. it would survive the service itself restarting.
    try (Connection connection =
            DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        PreparedStatement statement =
            connection.prepareStatement(
                "select is_on from greenhouse.actuator_state where device = ?")) {
      statement.setString(1, "restart-check");
      try (ResultSet resultSet = statement.executeQuery()) {
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getBoolean("is_on")).isTrue();
      }
    }
  }
}
