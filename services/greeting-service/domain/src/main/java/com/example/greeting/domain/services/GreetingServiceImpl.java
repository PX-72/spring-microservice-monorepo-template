package com.example.greeting.domain.services;

import com.example.greeting.domain.Greeting;
import com.example.greeting.domain.events.GreetingCreatedEvent;
import com.example.greeting.domain.ports.out.GreetingCache;
import com.example.greeting.domain.ports.out.GreetingEventPublisher;
import com.example.greeting.domain.ports.out.GreetingStore;
import java.util.Optional;
import java.util.UUID;

public class GreetingServiceImpl implements GreetingService {
  private final GreetingStore greetingStore;
  private final GreetingCache greetingCache;
  private final GreetingEventPublisher eventPublisher;

  public GreetingServiceImpl(
      GreetingStore greetingStore,
      GreetingCache greetingCache,
      GreetingEventPublisher eventPublisher) {
    this.greetingStore = greetingStore;
    this.greetingCache = greetingCache;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Greeting createGreeting(String name) {
    var greeting = new Greeting(UUID.randomUUID(), "Hello, " + name + "!");
    this.greetingStore.save(greeting);
    this.greetingCache.put(greeting);

    var event = GreetingCreatedEvent.from(greeting.id(), greeting.message());
    this.eventPublisher.publish(event);

    return greeting;
  }

  @Override
  public Optional<Greeting> getGreeting(UUID id) {
    return greetingCache
        .get(id)
        .or(
            () -> {
              var fromStore = greetingStore.findById(id);
              fromStore.ifPresent(greetingCache::put);
              return fromStore;
            });
  }
}
