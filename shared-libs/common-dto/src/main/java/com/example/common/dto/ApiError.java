package com.example.common.dto;

import java.time.Instant;
import java.util.List;

/**
 * Standard API error response following Problem Details (RFC 7807) principles.
 *
 * @param type URI reference identifying the error type
 * @param title Short human-readable summary
 * @param status HTTP status code
 * @param detail Human-readable explanation specific to this occurrence
 * @param instance URI reference identifying this specific occurrence
 * @param timestamp When the error occurred
 * @param errors Field-level validation errors, if applicable
 */
public record ApiError(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    Instant timestamp,
    List<FieldError> errors) {

  public record FieldError(String field, String message, Object rejectedValue) {}

  public static ApiError of(int status, String title, String detail) {
    return new ApiError(null, title, status, detail, null, Instant.now(), List.of());
  }

  public static ApiError of(int status, String title, String detail, List<FieldError> errors) {
    return new ApiError(null, title, status, detail, null, Instant.now(), errors);
  }
}
