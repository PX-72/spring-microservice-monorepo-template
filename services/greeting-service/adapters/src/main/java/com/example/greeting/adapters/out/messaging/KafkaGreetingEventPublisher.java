package com.example.greeting.adapters.out.messaging;

import com.example.greeting.domain.events.GreetingCreatedEvent;
import com.example.greeting.domain.ports.out.GreetingEventPublisher;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaGreetingEventPublisher implements GreetingEventPublisher {

  private static final Logger logger = LoggerFactory.getLogger(KafkaGreetingEventPublisher.class);
  private static final String TOPIC = "greeting-events";

  private final KafkaTemplate<String, GreetingCreatedEvent> kafkaTemplate;
  private final Counter publishSuccessCounter;
  private final Counter publishFailureCounter;

  public KafkaGreetingEventPublisher(
      KafkaTemplate<String, GreetingCreatedEvent> kafkaTemplate, MeterRegistry meterRegistry) {
    this.kafkaTemplate = kafkaTemplate;

    this.publishSuccessCounter =
        Counter.builder("kafka.greeting.events.published")
            .description("Number of successfully published greeting events")
            .tag("topic", TOPIC)
            .tag("outcome", "success")
            .register(meterRegistry);
    this.publishFailureCounter =
        Counter.builder("kafka.greeting.events.published")
            .description("Number of failed greeting event publications")
            .tag("topic", TOPIC)
            .tag("outcome", "failure")
            .register(meterRegistry);
  }

  @Override
  public void publish(GreetingCreatedEvent event) {
    logger.info(
        "Publishing greeting event eventId={} greetingId={}", event.eventId(), event.greetingId());
    kafkaTemplate
        .send(TOPIC, event.greetingId().toString(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                publishFailureCounter.increment();
                logger.error(
                    "Failed to publish event eventId={} greetingId={}: {}",
                    event.eventId(),
                    event.greetingId(),
                    ex.getMessage());
              } else {
                publishSuccessCounter.increment();
                logger.debug(
                    "Event published eventId={} greetingId={} partition={} offset={}",
                    event.eventId(),
                    event.greetingId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
              }
            });
  }
}
