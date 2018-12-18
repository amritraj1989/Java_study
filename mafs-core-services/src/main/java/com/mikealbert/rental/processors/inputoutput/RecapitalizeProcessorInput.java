package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.exception.MalBusinessException;

@Component("recapitalizeProcessorInput")
@Scope(value="prototype")
public class RecapitalizeProcessorInput extends BaseRentalProcessorInput{

	private BigDecimal  amount ;
	private BigDecimal adjustment;
	private BigDecimal profitAmount;   
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getAdjustment() {
		return adjustment;
	}
	public void setAdjustment(BigDecimal adjustment) {
		this.adjustment = adjustment;
	}
	public BigDecimal getProfitAmount() {
		return profitAmount;
	}
	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}
	
	@Override
	public void bindFinanceParamer(int index, BigDecimal value)	throws MalBusinessException {
		if(index == 2){
			this.setAmount(value);
		}else if(index == 3){
			this.setAdjustment(value);
		}else if(index == 4){
			this.setProfitAmount(value);
		}else{
			throw new MalBusinessException("wrong parameter index");
		}	
	}
	

	
}
