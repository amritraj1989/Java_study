package com.mikealbert.data.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.util.MALUtilities;

public class MANotNullValidator implements ConstraintValidator<MANotNull, Object>{
	@SuppressWarnings("unused")
	private MANotNull malNotNull;	
	
	public void initialize(MANotNull malNotNull) {		
		this.malNotNull = malNotNull;
	}

	public boolean isValid(Object value, ConstraintValidatorContext arg1) {
		
		boolean isValid = ! MALUtilities.isEmpty(value);
		
		return isValid;
	}

}
