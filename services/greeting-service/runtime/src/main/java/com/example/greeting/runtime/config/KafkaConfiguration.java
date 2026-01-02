package com.example.greeting.runtime.config;

import com.example.greeting.domain.events.GreetingCreatedEvent;
import com.example.greeting.domain.ports.in.GreetingEventHandler;
import com.example.greeting.domain.services.LoggingGreetingEventHandler;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfiguration {

  @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.group-id:greeting-service-group}")
  private String consumerGroupId;

  @Bean
  public ProducerFactory<String, GreetingCreatedEvent> greetingEventProducerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    config.put(ProducerConfig.ACKS_CONFIG, "all");
    config.put(ProducerConfig.RETRIES_CONFIG, 3);
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, GreetingCreatedEvent> kafkaTemplate() {
    KafkaTemplate<String, GreetingCreatedEvent> template =
        new KafkaTemplate<>(greetingEventProducerFactory());
    template.setObservationEnabled(true);
    return template;
  }

  @Bean
  public ConsumerFactory<String, GreetingCreatedEvent> greetingEventConsumerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

    JsonDeserializer<GreetingCreatedEvent> deserializer =
        new JsonDeserializer<>(GreetingCreatedEvent.class);
    deserializer.addTrustedPackages("com.example.greeting.domain.events");

    return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, GreetingCreatedEvent>
      greetingEventListenerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, GreetingCreatedEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(greetingEventConsumerFactory());
    factory.setConcurrency(3);
    factory.getContainerProperties().setObservationEnabled(true);
    return factory;
  }

  @Bean
  public GreetingEventHandler greetingEventHandler() {
    return new LoggingGreetingEventHandler();
  }
}
