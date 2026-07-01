package dev.pigmeo.ahc.greenhouse.application.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.pigmeo.ahc.greenhouse.domain.service.LightCycleService;
import dev.pigmeo.ahc.greenhouse.infrastructure.persistence.LightCycleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LightCycleController.class)
@Import(LightCycleService.class)
class LightCycleControllerTest {

  @MockitoBean private LightCycleRepository lightCycleRepository;

  @Autowired private MockMvc mockMvc;

  @Test
  void create_withValidNameAndCrons_persistsAndReturnsCreatedCycle() throws Exception {
    when(lightCycleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc
        .perform(
            post("/api/greenhouse/light-cycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\": \"Veg\", \"onCron\": \"0 0 8 * * *\", \"offCron\": \"0 0 20 * *"
                        + " *\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Veg"))
        .andExpect(jsonPath("$.onCron").value("0 0 8 * * *"))
        .andExpect(jsonPath("$.offCron").value("0 0 20 * * *"))
        .andExpect(jsonPath("$.active").value(false));

    verify(lightCycleRepository).save(any());
  }

  @Test
  void create_withInvalidOnCron_returns400AndDoesNotPersist() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/light-cycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\": \"Veg\", \"onCron\": \"not a cron\", \"offCron\": \"0 0 20 * * *\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"))
        .andExpect(
            jsonPath("$.details[0]").value("onCron: must be a valid 6-field cron expression"));

    verify(lightCycleRepository, never()).save(any());
  }

  @Test
  void create_withInvalidOffCron_returns400AndDoesNotPersist() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/light-cycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\": \"Veg\", \"onCron\": \"0 0 8 * * *\", \"offCron\": \"not a cron\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"))
        .andExpect(
            jsonPath("$.details[0]").value("offCron: must be a valid 6-field cron expression"));

    verify(lightCycleRepository, never()).save(any());
  }

  @Test
  void create_withBlankName_returns400AndDoesNotPersist() throws Exception {
    mockMvc
        .perform(
            post("/api/greenhouse/light-cycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\": \"\", \"onCron\": \"0 0 8 * * *\", \"offCron\": \"0 0 20 * * *\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"));

    verify(lightCycleRepository, never()).save(any());
  }
}
