package com.example.common.utils;

import java.util.UUID;

/** Utility class for UUID generation with support for testing. */
public final class UuidGenerator {

  private static UuidGenerator instance = new UuidGenerator();

  private UuidGenerator() {}

  public static UuidGenerator getInstance() {
    return instance;
  }

  /** Generate a new random UUID. */
  public UUID generate() {
    return UUID.randomUUID();
  }

  /** Parse a UUID from string, returning null if invalid. */
  public UUID parseOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(value.trim());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /** Check if a string is a valid UUID format. */
  public boolean isValid(String value) {
    return parseOrNull(value) != null;
  }

  /**
   * Replace the singleton instance (for testing purposes).
   *
   * @param generator the generator to use, or null to reset to default
   */
  public static void setInstance(UuidGenerator generator) {
    instance = generator != null ? generator : new UuidGenerator();
  }

  /** Reset to the default generator. */
  public static void reset() {
    instance = new UuidGenerator();
  }
}
