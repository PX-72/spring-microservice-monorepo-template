package com.example.greeting.domain.ports.out;

import com.example.greeting.domain.Greeting;
import java.util.Optional;
import java.util.UUID;

public interface GreetingStore {
  void save(Greeting greeting);

  Optional<Greeting> findById(UUID id);
}
