package com.example.greeting.domain.events;

import java.time.Instant;
import java.util.UUID;

public record GreetingCreatedEvent(
    UUID eventId, UUID greetingId, String message, Instant createdAt) {

  public static GreetingCreatedEvent from(UUID greetingId, String message) {
    return new GreetingCreatedEvent(UUID.randomUUID(), greetingId, message, Instant.now());
  }
}
