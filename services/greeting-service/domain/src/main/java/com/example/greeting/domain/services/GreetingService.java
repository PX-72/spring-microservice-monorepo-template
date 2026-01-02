package com.example.greeting.domain.services;

import com.example.greeting.domain.Greeting;
import java.util.Optional;
import java.util.UUID;

public interface GreetingService {
  Greeting createGreeting(String name);

  Optional<Greeting> getGreeting(UUID id);
}
