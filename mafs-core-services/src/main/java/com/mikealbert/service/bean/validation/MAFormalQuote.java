package com.mikealbert.service.bean.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = MAFormalQuoteValidator.class)
@Target( { TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface MAFormalQuote {
	String message() default "Formal Quote input is not correct";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
