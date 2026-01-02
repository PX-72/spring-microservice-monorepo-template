package com.example.greeting.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.greeting.domain.Greeting;
import com.example.greeting.domain.ports.out.GreetingCache;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class RedisCacheIT {

  @Container static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  @Container
  static GenericContainer<?> redis =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

  @Container
  static KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("grpc.server.port", () -> "-1");
  }

  @Autowired private GreetingCache greetingCache;

  @Test
  void shouldCacheAndRetrieveGreeting() {
    var greeting = new Greeting(UUID.randomUUID(), "Hello, Redis!");

    greetingCache.put(greeting);
    var retrieved = greetingCache.get(greeting.id());

    assertThat(retrieved).contains(greeting);
  }

  @Test
  void shouldReturnEmptyWhenNotCached() {
    var id = UUID.randomUUID();

    var retrieved = greetingCache.get(id);

    assertThat(retrieved).isEmpty();
  }

  @Test
  void shouldEvictGreeting() {
    var greeting = new Greeting(UUID.randomUUID(), "To be evicted");
    greetingCache.put(greeting);

    greetingCache.evict(greeting.id());

    assertThat(greetingCache.get(greeting.id())).isEmpty();
  }

  @Test
  void shouldEvictAllGreetings() {
    var greeting1 = new Greeting(UUID.randomUUID(), "First");
    var greeting2 = new Greeting(UUID.randomUUID(), "Second");
    greetingCache.put(greeting1);
    greetingCache.put(greeting2);

    greetingCache.evictAll();

    assertThat(greetingCache.get(greeting1.id())).isEmpty();
    assertThat(greetingCache.get(greeting2.id())).isEmpty();
  }
}
