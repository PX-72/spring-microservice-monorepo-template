package com.example.common.dto;

import java.util.List;

/**
 * Standard paginated response wrapper.
 *
 * @param content The page content
 * @param page Current page number (zero-based)
 * @param size Page size
 * @param totalElements Total number of elements across all pages
 * @param totalPages Total number of pages
 * @param <T> Type of elements in the page
 */
public record PageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
    int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    return new PageResponse<>(content, page, size, totalElements, totalPages);
  }

  public static <T> PageResponse<T> empty(int page, int size) {
    return new PageResponse<>(List.of(), page, size, 0, 0);
  }

  public boolean hasNext() {
    return page < totalPages - 1;
  }

  public boolean hasPrevious() {
    return page > 0;
  }

  public boolean isEmpty() {
    return content.isEmpty();
  }
}
