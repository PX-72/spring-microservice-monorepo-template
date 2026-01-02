package com.example.greeting.domain.ports.out;

import com.example.greeting.domain.Greeting;
import java.util.Optional;
import java.util.UUID;

public interface GreetingCache {
  Optional<Greeting> get(UUID id);

  void put(Greeting greeting);

  void evict(UUID id);

  void evictAll();
}
