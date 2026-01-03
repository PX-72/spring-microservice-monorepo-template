package com.example.common.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResponseTest {

  @Test
  void of_calculatesTotalPages() {
    var response = PageResponse.of(List.of("a", "b", "c"), 0, 10, 25);

    assertEquals(3, response.totalPages());
    assertEquals(25, response.totalElements());
  }

  @Test
  void of_calculatesTotalPagesExactDivision() {
    var response = PageResponse.of(List.of("a", "b"), 0, 10, 20);

    assertEquals(2, response.totalPages());
  }

  @Test
  void of_handlesZeroTotal() {
    var response = PageResponse.of(List.of(), 0, 10, 0);

    assertEquals(0, response.totalPages());
    assertTrue(response.isEmpty());
  }

  @Test
  void empty_createsEmptyResponse() {
    var response = PageResponse.empty(0, 10);

    assertTrue(response.content().isEmpty());
    assertEquals(0, response.totalElements());
    assertEquals(0, response.totalPages());
  }

  @Test
  void hasNext_returnsTrueWhenMorePages() {
    var response = PageResponse.of(List.of("a"), 0, 10, 25);

    assertTrue(response.hasNext());
  }

  @Test
  void hasNext_returnsFalseOnLastPage() {
    var response = PageResponse.of(List.of("a"), 2, 10, 25);

    assertFalse(response.hasNext());
  }

  @Test
  void hasPrevious_returnsFalseOnFirstPage() {
    var response = PageResponse.of(List.of("a"), 0, 10, 25);

    assertFalse(response.hasPrevious());
  }

  @Test
  void hasPrevious_returnsTrueAfterFirstPage() {
    var response = PageResponse.of(List.of("a"), 1, 10, 25);

    assertTrue(response.hasPrevious());
  }

  @Test
  void isEmpty_returnsTrueForEmptyContent() {
    var response = PageResponse.of(List.of(), 0, 10, 0);

    assertTrue(response.isEmpty());
  }

  @Test
  void isEmpty_returnsFalseForNonEmptyContent() {
    var response = PageResponse.of(List.of("a"), 0, 10, 1);

    assertFalse(response.isEmpty());
  }

  @Test
  void content_preservesOrder() {
    var items = List.of("first", "second", "third");
    var response = PageResponse.of(items, 0, 10, 3);

    assertEquals(items, response.content());
  }
}
