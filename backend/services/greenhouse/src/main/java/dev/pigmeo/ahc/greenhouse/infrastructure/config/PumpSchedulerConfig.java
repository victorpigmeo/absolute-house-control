package dev.pigmeo.ahc.greenhouse.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class PumpSchedulerConfig {

  private static final Logger log = LoggerFactory.getLogger(PumpSchedulerConfig.class);

  @Bean
  TaskScheduler pumpTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setThreadNamePrefix("pump-scheduler-");
    scheduler.setErrorHandler(
        ex ->
            log.error(
                "Scheduled greenhouse actuator command failed; device state may now be stale", ex));
    scheduler.initialize();
    return scheduler;
  }
}
