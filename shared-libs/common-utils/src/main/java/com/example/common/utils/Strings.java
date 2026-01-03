package com.example.common.utils;

/** Common string utilities. */
public final class Strings {

  private Strings() {}

  /** Returns true if the string is null or empty after trimming. */
  public static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  /** Returns true if the string is not null and not empty after trimming. */
  public static boolean isNotBlank(String value) {
    return !isBlank(value);
  }

  /** Returns null if blank, otherwise returns the trimmed value. */
  public static String trimToNull(String value) {
    if (isBlank(value)) {
      return null;
    }
    return value.trim();
  }

  /** Returns empty string if null, otherwise returns the value. */
  public static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }

  /** Truncates a string to the specified max length, appending suffix if truncated. */
  public static String truncate(String value, int maxLength, String suffix) {
    if (value == null || value.length() <= maxLength) {
      return value;
    }
    String s = suffix != null ? suffix : "";
    int endIndex = Math.max(0, maxLength - s.length());
    return value.substring(0, endIndex) + s;
  }

  /** Truncates a string to the specified max length with "..." suffix. */
  public static String truncate(String value, int maxLength) {
    return truncate(value, maxLength, "...");
  }
}
