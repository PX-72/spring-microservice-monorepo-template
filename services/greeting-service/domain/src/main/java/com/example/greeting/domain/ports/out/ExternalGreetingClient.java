package com.example.greeting.domain.ports.out;

import com.example.greeting.domain.Greeting;
import java.util.Optional;
import java.util.UUID;

public interface ExternalGreetingClient {
  Optional<Greeting> fetchGreeting(UUID id);

  Greeting createRemoteGreeting(String name);
}
