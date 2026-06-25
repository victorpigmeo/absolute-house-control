package dev.pigmeo.ahc.greenhouse.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.Environment;

class GreenhouseProfileConfigTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withInitializer(new ConfigDataApplicationContextInitializer());

  @Test
  void noActiveProfile_defaultsToLocalAndResolvesLocalDatasource() {
    contextRunner.run(
        context -> {
          Environment environment = context.getEnvironment();
          assertThat(environment.getActiveProfiles()).isEmpty();
          assertThat(environment.getProperty("spring.datasource.url"))
              .isEqualTo("jdbc:postgresql://db:5432/absolute_house_control");
          assertThat(environment.getProperty("spring.datasource.password"))
              .isEqualTo("sa_absolute_house_control");
          assertThat(environment.getProperty("spring.datasource.username"))
              .isEqualTo("sa_absolute_house_control");
        });
  }

  @Test
  void explicitLocalProfile_resolvesSameLocalDefaults() {
    contextRunner
        .withPropertyValues("spring.profiles.active=local")
        .run(
            context -> {
              Environment environment = context.getEnvironment();
              assertThat(environment.getProperty("spring.datasource.url"))
                  .isEqualTo("jdbc:postgresql://db:5432/absolute_house_control");
            });
  }

  @Test
  void prodProfile_withoutRequiredEnvVars_failsToResolveDatasourceUrlPlaceholder() {
    contextRunner
        .withPropertyValues("spring.profiles.active=prod")
        .run(
            context -> {
              Environment environment = context.getEnvironment();
              assertThat(environment.getActiveProfiles()).containsExactly("prod");
              assertThatThrownBy(() -> environment.getProperty("spring.datasource.url"))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("Could not resolve placeholder")
                  .hasMessageContaining("GREENHOUSE_DB_URL");
            });
  }

  @Test
  void prodProfile_withoutRequiredEnvVars_failsToResolveDatasourcePasswordPlaceholder() {
    contextRunner
        .withPropertyValues("spring.profiles.active=prod")
        .run(
            context ->
                assertThatThrownBy(
                        () -> context.getEnvironment().getProperty("spring.datasource.password"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Could not resolve placeholder")
                    .hasMessageContaining("GREENHOUSE_DB_PASSWORD"));
  }

  @Test
  void prodProfile_withAllRequiredEnvVarsSupplied_resolvesSuccessfully() {
    contextRunner
        .withPropertyValues(
            "spring.profiles.active=prod",
            "GREENHOUSE_DB_URL=jdbc:postgresql://prod-pg:5432/house_control",
            "GREENHOUSE_DB_PASSWORD=supplied-secret")
        .run(
            context -> {
              Environment environment = context.getEnvironment();
              assertThat(environment.getProperty("spring.datasource.url"))
                  .isEqualTo("jdbc:postgresql://prod-pg:5432/house_control");
              assertThat(environment.getProperty("spring.datasource.password"))
                  .isEqualTo("supplied-secret");
              assertThat(environment.getProperty("spring.datasource.username"))
                  .isEqualTo("sa_absolute_house_control");
            });
  }
}
