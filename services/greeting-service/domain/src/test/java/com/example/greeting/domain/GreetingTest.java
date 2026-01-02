package com.example.greeting.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class GreetingTest {
  @Test
  void GreetingStoresFields() {
    var id = UUID.randomUUID();
    var greeting = new Greeting(id, "Hello");

    assertNotNull(greeting);
    assertEquals(id, greeting.id());
    assertEquals("Hello", greeting.message());
  }
}
