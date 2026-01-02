package com.example.greeting.domain.ports.in;

import com.example.greeting.domain.events.GreetingCreatedEvent;

public interface GreetingEventHandler {
  void handle(GreetingCreatedEvent event);
}
