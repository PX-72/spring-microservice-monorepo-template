package com.example.common.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PageRequestTest {

  @Test
  void of_createsWithPageAndSize() {
    var request = PageRequest.of(2, 25);

    assertEquals(2, request.page());
    assertEquals(25, request.size());
    assertNull(request.sortBy());
    assertNull(request.sortDirection());
  }

  @Test
  void ofDefault_createsWithDefaults() {
    var request = PageRequest.ofDefault();

    assertEquals(0, request.page());
    assertEquals(20, request.size());
  }

  @Test
  void constructor_normalizesNegativePage() {
    var request = new PageRequest(-5, 10, null, null);

    assertEquals(0, request.page());
  }

  @Test
  void constructor_normalizesZeroSize() {
    var request = new PageRequest(0, 0, null, null);

    assertEquals(20, request.size());
  }

  @Test
  void constructor_normalizesNegativeSize() {
    var request = new PageRequest(0, -10, null, null);

    assertEquals(20, request.size());
  }

  @Test
  void constructor_normalizesExcessiveSize() {
    var request = new PageRequest(0, 500, null, null);

    assertEquals(20, request.size());
  }

  @Test
  void constructor_allowsMaxSize() {
    var request = new PageRequest(0, 100, null, null);

    assertEquals(100, request.size());
  }

  @Test
  void offset_calculatesCorrectly() {
    assertEquals(0, PageRequest.of(0, 10).offset());
    assertEquals(10, PageRequest.of(1, 10).offset());
    assertEquals(50, PageRequest.of(2, 25).offset());
  }

  @Test
  void sortDirection_supportsAscAndDesc() {
    var ascRequest =
        new PageRequest(0, 10, "createdAt", PageRequest.SortDirection.ASC);
    var descRequest =
        new PageRequest(0, 10, "createdAt", PageRequest.SortDirection.DESC);

    assertEquals(PageRequest.SortDirection.ASC, ascRequest.sortDirection());
    assertEquals(PageRequest.SortDirection.DESC, descRequest.sortDirection());
    assertEquals("createdAt", ascRequest.sortBy());
  }
}
