package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.exception.MalBusinessException;

@Component("zeroRentalProcessorInput")
@Scope(value="prototype")
public class ZeroRentalProcessorInput extends BaseRentalProcessorInput{

	@Override
	public void bindFinanceParamer(int index, BigDecimal value)	throws MalBusinessException {
		
	}
	
	
}
