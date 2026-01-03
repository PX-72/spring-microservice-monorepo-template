package com.example.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringsTest {

  @Test
  void isBlank_returnsTrueForNull() {
    assertTrue(Strings.isBlank(null));
  }

  @Test
  void isBlank_returnsTrueForEmptyString() {
    assertTrue(Strings.isBlank(""));
  }

  @Test
  void isBlank_returnsTrueForWhitespaceOnly() {
    assertTrue(Strings.isBlank("   "));
    assertTrue(Strings.isBlank("\t\n"));
  }

  @Test
  void isBlank_returnsFalseForNonBlank() {
    assertFalse(Strings.isBlank("hello"));
    assertFalse(Strings.isBlank(" hello "));
  }

  @Test
  void isNotBlank_returnsFalseForNull() {
    assertFalse(Strings.isNotBlank(null));
  }

  @Test
  void isNotBlank_returnsFalseForEmptyString() {
    assertFalse(Strings.isNotBlank(""));
  }

  @Test
  void isNotBlank_returnsTrueForNonBlank() {
    assertTrue(Strings.isNotBlank("hello"));
    assertTrue(Strings.isNotBlank(" hello "));
  }

  @Test
  void trimToNull_returnsNullForNull() {
    assertNull(Strings.trimToNull(null));
  }

  @Test
  void trimToNull_returnsNullForBlank() {
    assertNull(Strings.trimToNull(""));
    assertNull(Strings.trimToNull("   "));
  }

  @Test
  void trimToNull_returnsTrimmedValue() {
    assertEquals("hello", Strings.trimToNull("  hello  "));
    assertEquals("hello", Strings.trimToNull("hello"));
  }

  @Test
  void nullToEmpty_returnsEmptyForNull() {
    assertEquals("", Strings.nullToEmpty(null));
  }

  @Test
  void nullToEmpty_returnsValueForNonNull() {
    assertEquals("hello", Strings.nullToEmpty("hello"));
    assertEquals("", Strings.nullToEmpty(""));
  }

  @Test
  void truncate_returnsNullForNull() {
    assertNull(Strings.truncate(null, 10));
  }

  @Test
  void truncate_returnsOriginalIfShorter() {
    assertEquals("hello", Strings.truncate("hello", 10));
  }

  @Test
  void truncate_returnsOriginalIfExactLength() {
    assertEquals("hello", Strings.truncate("hello", 5));
  }

  @Test
  void truncate_truncatesWithDefaultSuffix() {
    assertEquals("hel...", Strings.truncate("hello world", 6));
  }

  @Test
  void truncate_truncatesWithCustomSuffix() {
    assertEquals("hello~", Strings.truncate("hello world", 6, "~"));
  }

  @Test
  void truncate_handlesNullSuffix() {
    assertEquals("hello ", Strings.truncate("hello world", 6, null));
  }

  @Test
  void truncate_handlesEmptySuffix() {
    assertEquals("hello ", Strings.truncate("hello world", 6, ""));
  }

  @Test
  void truncate_handlesVeryShortMaxLength() {
    assertEquals("...", Strings.truncate("hello world", 3));
    assertEquals(".", Strings.truncate("hello world", 1, "."));
  }

  @Test
  void truncate_handlesZeroMaxLength() {
    assertEquals("...", Strings.truncate("hello", 0));
  }
}
