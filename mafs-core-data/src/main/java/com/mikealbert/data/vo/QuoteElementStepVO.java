package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class QuoteElementStepVO {
	
	private Long id;
	private BigDecimal originalCost;
	private BigDecimal startCapital;
    private BigDecimal endCapital;
    private BigDecimal rental;
    private BigDecimal elementOriginalCost;//this variable is needed because originalCost is not same as element's capital cost for OE revision.
    
    public  QuoteElementStepVO(BigDecimal originalCost, BigDecimal startCapital, BigDecimal endCapital){
    	this.originalCost = originalCost;
    	this.startCapital = startCapital;
    	this.endCapital = endCapital;    	
    }
    
    public  QuoteElementStepVO(BigDecimal originalCost, BigDecimal startCapital, BigDecimal endCapital, BigDecimal elementOriginalCost){
    	this.originalCost = originalCost;
    	this.startCapital = startCapital;
    	this.endCapital = endCapital;    	
    	this.elementOriginalCost = elementOriginalCost;    	
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getOriginalCost() {
		return originalCost;
	}

	public void setOriginalCost(BigDecimal originalCost) {
		this.originalCost = originalCost;
	}

	public BigDecimal getStartCapital() {
		return startCapital;
	}

	public void setStartCapital(BigDecimal startCapital) {
		this.startCapital = startCapital;
	}

	public BigDecimal getEndCapital() {
		return endCapital;
	}

	public void setEndCapital(BigDecimal endCapital) {
		this.endCapital = endCapital;
	}

	public BigDecimal getRental() {
		return rental;
	}

	public void setRental(BigDecimal rental) {
		this.rental = rental;
	}

	@Override
	public String toString() {
		return "QuoteElementStepVO [originalCost=" + originalCost
				+ ", startCapital=" + startCapital + ", endCapital="
				+ endCapital + ", rental=" + rental + "]";
	}

	public BigDecimal getElementOriginalCost() {
		return elementOriginalCost;
	}

	public void setElementOriginalCost(BigDecimal elementOriginalCost) {
		this.elementOriginalCost = elementOriginalCost;
	}

}
