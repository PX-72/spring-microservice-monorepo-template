package com.example.greeting.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.greeting.domain.events.GreetingCreatedEvent;
import com.example.greeting.domain.services.GreetingService;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
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
class KafkaMessagingIT {

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

  @Autowired private GreetingService greetingService;

  @Test
  void shouldPublishEventWhenGreetingCreated() {
    JsonDeserializer<GreetingCreatedEvent> deserializer =
        new JsonDeserializer<>(GreetingCreatedEvent.class);
    deserializer.addTrustedPackages("com.example.greeting.domain.events");

    Map<String, Object> consumerProps =
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers(),
            ConsumerConfig.GROUP_ID_CONFIG,
            "test-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest");

    Consumer<String, GreetingCreatedEvent> consumer =
        new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer)
            .createConsumer();

    consumer.subscribe(Collections.singletonList("greeting-events"));

    var greeting = greetingService.createGreeting("Kafka Test");

    ConsumerRecords<String, GreetingCreatedEvent> records = consumer.poll(Duration.ofSeconds(10));

    assertThat(records.count()).isGreaterThan(0);
    var event = records.iterator().next().value();
    assertThat(event.greetingId()).isEqualTo(greeting.id());
    assertThat(event.message()).isEqualTo("Hello, Kafka Test!");

    consumer.close();
  }
}
