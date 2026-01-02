package com.example.greeting.adapters.in.rest;

import com.example.greeting.domain.services.GreetingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingController {
  private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);

  private final GreetingService greetingService;

  public GreetingController(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GreetingResponse create(@Valid @RequestBody CreateGreetingRequest request) {
    logger.info("Creating greeting");
    var greeting = this.greetingService.createGreeting(request.name());
    return new GreetingResponse(greeting.id(), greeting.message());
  }
}
