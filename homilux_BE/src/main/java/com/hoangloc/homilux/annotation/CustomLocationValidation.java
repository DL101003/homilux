package com.hoangloc.homilux.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomLocationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomLocationValidation {
    String message() default "Custom location address is required when location type is CUSTOM";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}