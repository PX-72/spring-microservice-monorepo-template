package com.example.common.dto;

/**
 * Standard pagination request parameters.
 *
 * @param page Zero-based page index
 * @param size Number of items per page
 * @param sortBy Field to sort by (optional)
 * @param sortDirection Sort direction: ASC or DESC (optional)
 */
public record PageRequest(int page, int size, String sortBy, SortDirection sortDirection) {

  public enum SortDirection {
    ASC,
    DESC
  }

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;
  public static final int MAX_SIZE = 100;

  public PageRequest {
    if (page < 0) {
      page = DEFAULT_PAGE;
    }
    if (size <= 0 || size > MAX_SIZE) {
      size = DEFAULT_SIZE;
    }
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size, null, null);
  }

  public static PageRequest ofDefault() {
    return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE, null, null);
  }

  public int offset() {
    return page * size;
  }
}
