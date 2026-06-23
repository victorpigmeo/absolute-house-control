package dev.pigmeo.ahc.greenhouse.application;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.pigmeo.ahc.greenhouse.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SmokeController.class)
@Import(SecurityConfig.class)
@TestPropertySource(
    properties =
        "spring.security.oauth2.resourceserver.jwt.issuer-uri="
            + "https://example-test-issuer.invalid/realms/house-control")
class SmokeControllerSecurityTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void pingWithoutAuth_returns401() throws Exception {
    mockMvc.perform(get("/api/greenhouse/ping")).andExpect(status().isUnauthorized());
  }

  @Test
  void pingWithValidJwt_returns200() throws Exception {
    mockMvc
        .perform(get("/api/greenhouse/ping").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.service").value("greenhouse"))
        .andExpect(jsonPath("$.status").value("ok"));
  }
}
