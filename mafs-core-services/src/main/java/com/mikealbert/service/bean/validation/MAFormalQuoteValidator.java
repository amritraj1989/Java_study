package com.mikealbert.service.bean.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.service.vo.FormalQuoteInput;
import com.mikealbert.util.MALUtilities;

public class MAFormalQuoteValidator implements ConstraintValidator<MAFormalQuote, FormalQuoteInput>{

	@Override
	public void initialize(MAFormalQuote constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(FormalQuoteInput value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		context.disableDefaultConstraintViolation();
		
		messages = validateClientAccount(value, messages);
		
		if(messages.isEmpty()) {
			isValid = true;
		} else {
			context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();
			isValid = false;
		}
		
		return isValid;
	}
	
	private List<String> validateClientAccount(FormalQuoteInput quote, List<String> messages) {
		if(MALUtilities.isEmpty(quote.getClient())) {
			messages.add("Client account is required");
		}	
		
		return messages;
	}

}
