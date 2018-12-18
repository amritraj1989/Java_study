package com.mikealbert.vision.beanvalidation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MADriverAddressesValidator.class)
@Documented
public @interface MADriverAddresses {
    String message() default "{field.addresses.check}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};    
    String label();
}
