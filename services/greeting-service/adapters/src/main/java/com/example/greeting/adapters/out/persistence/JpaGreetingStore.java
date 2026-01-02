package com.example.greeting.adapters.out.persistence;

import com.example.greeting.domain.Greeting;
import com.example.greeting.domain.ports.out.GreetingStore;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaGreetingStore implements GreetingStore {
  private final GreetingRepository greetingRepository;

  public JpaGreetingStore(GreetingRepository greetingRepository) {
    this.greetingRepository = greetingRepository;
  }

  @Override
  public void save(Greeting greeting) {
    var entity = new GreetingEntity(greeting.id(), greeting.message());
    this.greetingRepository.save(entity);
  }

  @Override
  public Optional<Greeting> findById(UUID id) {
    return greetingRepository
        .findById(id)
        .map(entity -> new Greeting(entity.getId(), entity.getMessage()));
  }
}
