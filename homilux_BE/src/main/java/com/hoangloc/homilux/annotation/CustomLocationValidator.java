package com.hoangloc.homilux.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomLocationValidator implements ConstraintValidator<CustomLocationValidation, Booking> {
    @Override
    public void initialize(CustomLocationValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext context) {
        if (booking.getLocationType() == LocationType.TUY_CHINH) {
            return booking.getCustomLocationAddress() != null && !booking.getCustomLocationAddress().isBlank();
        }
        return true;
    }
}