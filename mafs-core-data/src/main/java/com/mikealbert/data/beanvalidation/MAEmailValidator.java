package com.mikealbert.data.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.util.MALUtilities;

public class MAEmailValidator implements ConstraintValidator<MAEmail, Object>{
	@SuppressWarnings("unused")
	private MAEmail maEmail;	
	
	public void initialize(MAEmail maEmail) {		
		this.maEmail = maEmail;
	}

	public boolean isValid(Object value, ConstraintValidatorContext arg1) {
		
		boolean isValid = false;
		
		//Email validation should not validate email as a required value, so only
		// execute validation when email is not null.
		if(MALUtilities.isEmpty(value)){
			isValid = true;
		}
		else{
			isValid = MALUtilities.isValidEmailAddress(String.valueOf(value));
		}
		return isValid;
	}
}
