package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.exception.MalBusinessException;

@Component("amortizationProcessorInput")
@Scope(value="prototype") 
public class AmortizationProcessorInput extends BaseRentalProcessorInput{
	
private BigDecimal amount;

public BigDecimal getAmount() {
	return amount;
}

public void setAmount(BigDecimal amount) {
	this.amount = amount;
}

public void bindFinanceParamer(int index, BigDecimal  value) throws MalBusinessException{		
	if(index == 2){
		this.setAmount(value);
	}else{
		throw new MalBusinessException("wrong parameter index");
	}		
}

}