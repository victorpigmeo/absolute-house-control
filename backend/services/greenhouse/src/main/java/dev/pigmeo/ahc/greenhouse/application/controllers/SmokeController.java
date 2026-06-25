package dev.pigmeo.ahc.greenhouse.application.controllers;

import dev.pigmeo.ahc.greenhouse.application.dto.SmokeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greenhouse")
public class SmokeController {

  @GetMapping("/ping")
  public SmokeResponse ping() {
    return new SmokeResponse("greenhouse", "ok");
  }
}
