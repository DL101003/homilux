package com.hoangloc.homilux.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resource, String field, Object value) {
        super(String.format("%s đã tồn tại với %s: %s", resource, field, value));
    }
}
