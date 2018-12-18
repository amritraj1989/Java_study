package com.mikealbert.data.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.util.MALUtilities;

public class MASizeValidator implements ConstraintValidator<MASize, String> {
	private MASize malSize;

	public void initialize(MASize size) {
		this.malSize = size;
	}

	public boolean isValid(String value, ConstraintValidatorContext arg1) {

		boolean isValid = MALUtilities.validateFieldLength(
				Integer.valueOf(malSize.max()), Integer.valueOf(malSize.min()),
				value);
		
		return isValid;
	}

}
