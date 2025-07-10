package com.hoangloc.homilux.exceptions;

public class ResourceNotFoundException extends HomiLuxException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object fieldVale) {
        super(String.format("%s not found with id %s", resourceName, fieldVale));
    }
}