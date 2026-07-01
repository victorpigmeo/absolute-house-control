package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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
class LightCycleRepositoryIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:17").withDatabaseName("house_control");

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private LightCycleRepository repository;

  @Test
  void save_persistsNameAndCronsAndDefaultsActiveToFalse() {
    LightCycle saved = repository.save(new LightCycle("Veg", "0 0 8 * * *", "0 0 20 * * *"));

    LightCycle found = repository.findById(saved.getId()).orElseThrow();
    assertThat(found.getName()).isEqualTo("Veg");
    assertThat(found.getOnCron()).isEqualTo("0 0 8 * * *");
    assertThat(found.getOffCron()).isEqualTo("0 0 20 * * *");
    assertThat(found.isActive()).isFalse();
    assertThat(found.getCreatedAt()).isNotNull();
    assertThat(found.getVersion()).isEqualTo(1L);
  }
}
