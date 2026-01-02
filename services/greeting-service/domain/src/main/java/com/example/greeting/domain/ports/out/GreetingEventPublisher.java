package com.example.greeting.domain.ports.out;

import com.example.greeting.domain.events.GreetingCreatedEvent;

public interface GreetingEventPublisher {
  void publish(GreetingCreatedEvent event);
}
