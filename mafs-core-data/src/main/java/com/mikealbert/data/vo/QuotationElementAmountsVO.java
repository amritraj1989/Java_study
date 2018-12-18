package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class QuotationElementAmountsVO {
	private BigDecimal rentalAmount;
	private BigDecimal overheadAmount;
	private BigDecimal profitAmount;
	private BigDecimal elementCost;
	private String rentalFound;
	

	public BigDecimal getRentalAmount() {
		return rentalAmount;
	}
	
	public void setRentalAmount(BigDecimal rentalAmount) {
		this.rentalAmount = rentalAmount;
	}
	
	public BigDecimal getOverheadAmount() {
		return overheadAmount;
	}
	
	public void setOverheadAmount(BigDecimal overheadAmount) {
		this.overheadAmount = overheadAmount;
	}
	
	public BigDecimal getProfitAmount() {
		return profitAmount;
	}
	
	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}
	
	public BigDecimal getElementCost() {
		return elementCost;
	}
	
	public void setElementCost(BigDecimal elementCost) {
		this.elementCost = elementCost;
	}
	
	public String getRentalFound() {
		return rentalFound;
	}
	
	public void setRentalFound(String rentalFound) {
		this.rentalFound = rentalFound;
	}

}
