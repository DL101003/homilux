package com.hoangloc.homilux.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String resource, String field, Object value) {
    super(String.format("%s không tồn tại với %s: %s", resource, field, value));
  }
}
