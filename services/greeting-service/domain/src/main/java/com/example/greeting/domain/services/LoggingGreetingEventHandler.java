package com.example.greeting.domain.services;

import com.example.greeting.domain.events.GreetingCreatedEvent;
import com.example.greeting.domain.ports.in.GreetingEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingGreetingEventHandler implements GreetingEventHandler {

  private static final Logger logger = LoggerFactory.getLogger(LoggingGreetingEventHandler.class);

  @Override
  public void handle(GreetingCreatedEvent event) {
    logger.info(
        "Processing greeting event: id={}, greetingId={}, message='{}', createdAt={}",
        event.eventId(),
        event.greetingId(),
        event.message(),
        event.createdAt());
  }
}
