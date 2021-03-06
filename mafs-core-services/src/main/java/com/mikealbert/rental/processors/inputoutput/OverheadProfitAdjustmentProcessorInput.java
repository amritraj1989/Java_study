package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.exception.MalBusinessException;

@Component("overheadProfitAdjustmentProcessorInput")
@Scope(value="prototype")
public class OverheadProfitAdjustmentProcessorInput extends BaseRentalProcessorInput{

	private BigDecimal overhead;  
	private BigDecimal overheadProfit;
	private BigDecimal  overheadAdjust;
	
	public BigDecimal getOverhead() {
		return overhead;
	}

	public void setOverhead(BigDecimal overhead) {
		this.overhead = overhead;
	}

	public BigDecimal getOverheadProfit() {
		return overheadProfit;
	}

	public void setOverheadProfit(BigDecimal overheadProfit) {
		this.overheadProfit = overheadProfit;
	}

	public BigDecimal getOverheadAdjust() {
		return overheadAdjust;
	}

	public void setOverheadAdjust(BigDecimal overheadAdjust) {
		this.overheadAdjust = overheadAdjust;
	}
	
	@Override
	public void bindFinanceParamer(int index, BigDecimal value)	throws MalBusinessException {
		if(index == 2){
			this.setOverhead(value);
		}else if(index == 3){
			this.setOverheadProfit(value);
		}else if(index == 4){
			this.setOverheadAdjust(value);
		}else{
			throw new MalBusinessException("wrong parameter index");
		}	
	}
	
	
	
	
}
