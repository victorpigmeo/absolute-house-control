package dev.pigmeo.ahc.greenhouse.application.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SmokeController.class)
class SmokeControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void pingReturnsOk() throws Exception {
    mockMvc
        .perform(get("/api/greenhouse/ping"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.service").value("greenhouse"))
        .andExpect(jsonPath("$.status").value("ok"));
  }
}
