package com.example.greeting.adapters.in.messaging;

import com.example.greeting.domain.events.GreetingCreatedEvent;
import com.example.greeting.domain.ports.in.GreetingEventHandler;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaGreetingEventListener {

  private static final Logger logger = LoggerFactory.getLogger(KafkaGreetingEventListener.class);

  private final GreetingEventHandler eventHandler;
  private final Counter eventsReceivedCounter;
  private final Counter eventsProcessedCounter;
  private final Counter eventsFailedCounter;
  private final Timer eventProcessingTimer;

  public KafkaGreetingEventListener(
      GreetingEventHandler eventHandler, MeterRegistry meterRegistry) {
    this.eventHandler = eventHandler;

    this.eventsReceivedCounter =
        Counter.builder("kafka.greeting.events.received")
            .description("Number of greeting events received")
            .register(meterRegistry);
    this.eventsProcessedCounter =
        Counter.builder("kafka.greeting.events.processed")
            .description("Number of greeting events successfully processed")
            .tag("outcome", "success")
            .register(meterRegistry);
    this.eventsFailedCounter =
        Counter.builder("kafka.greeting.events.processed")
            .description("Number of greeting events that failed processing")
            .tag("outcome", "failure")
            .register(meterRegistry);
    this.eventProcessingTimer =
        Timer.builder("kafka.greeting.events.processing.time")
            .description("Time to process greeting events")
            .register(meterRegistry);
  }

  @KafkaListener(
      topics = "greeting-events",
      groupId = "${spring.kafka.consumer.group-id:greeting-service-group}",
      containerFactory = "greetingEventListenerFactory")
  public void onGreetingCreated(GreetingCreatedEvent event) {
    eventsReceivedCounter.increment();
    logger.info(
        "Received greeting event eventId={} greetingId={}", event.eventId(), event.greetingId());

    eventProcessingTimer.record(
        () -> {
          try {
            eventHandler.handle(event);
            eventsProcessedCounter.increment();
            logger.debug("Processed greeting event eventId={}", event.eventId());
          } catch (Exception e) {
            eventsFailedCounter.increment();
            logger.error(
                "Failed to process event eventId={} greetingId={}: {}",
                event.eventId(),
                event.greetingId(),
                e.getMessage(),
                e);
            throw e;
          }
        });
  }
}
