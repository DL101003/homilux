package com.hoangloc.homilux.annotation;

import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.util.LocationType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomLocationValidator implements ConstraintValidator<CustomLocationValidation, Event> {
    @Override
    public void initialize(CustomLocationValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        if (event.getLocationType() == LocationType.CUSTOM) {
            return event.getCustomLocation() != null && !event.getCustomLocation().isBlank();
        }
        return true;
    }
}