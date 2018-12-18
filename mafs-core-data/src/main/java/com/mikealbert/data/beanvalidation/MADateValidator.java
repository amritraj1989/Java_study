package com.mikealbert.data.beanvalidation;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.util.MALUtilities;

public class MADateValidator implements ConstraintValidator<MADate, Object>{
	
	@SuppressWarnings("unused")
	private MADate maDate;	
	
	public void initialize(MADate maDate) {		
		this.maDate = maDate;
	}

	public boolean isValid(Object value, ConstraintValidatorContext arg1) {
		Date date = (Date)value;
		
		if (MALUtilities.isEmpty(value)) return true;
		
		return MALUtilities.validateDateRange(date);
	}

}
