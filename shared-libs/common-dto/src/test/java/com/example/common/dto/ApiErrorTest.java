package com.example.common.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

  @Test
  void of_createsErrorWithBasicFields() {
    var error = ApiError.of(400, "Bad Request", "Invalid input");

    assertEquals(400, error.status());
    assertEquals("Bad Request", error.title());
    assertEquals("Invalid input", error.detail());
    assertNotNull(error.timestamp());
    assertTrue(error.errors().isEmpty());
    assertNull(error.type());
    assertNull(error.instance());
  }

  @Test
  void of_createsErrorWithFieldErrors() {
    var fieldErrors =
        List.of(
            new ApiError.FieldError("name", "must not be blank", null),
            new ApiError.FieldError("email", "must be a valid email", "invalid"));

    var error = ApiError.of(400, "Validation Failed", "Request validation failed", fieldErrors);

    assertEquals(400, error.status());
    assertEquals(2, error.errors().size());
    assertEquals("name", error.errors().get(0).field());
    assertEquals("must not be blank", error.errors().get(0).message());
    assertNull(error.errors().get(0).rejectedValue());
    assertEquals("invalid", error.errors().get(1).rejectedValue());
  }

  @Test
  void record_supportsAllFields() {
    var error =
        new ApiError(
            "urn:error:validation",
            "Validation Error",
            422,
            "Field validation failed",
            "/api/v1/users/123",
            java.time.Instant.now(),
            List.of());

    assertEquals("urn:error:validation", error.type());
    assertEquals("/api/v1/users/123", error.instance());
  }
}
