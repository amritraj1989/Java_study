package com.mikealbert.vision.specs.rentalcalcs;

import java.math.BigDecimal;

public class RentalCalcsCompareVO {
	private BigDecimal totalRental;
	private BigDecimal profitPercentage;
	private BigDecimal profitDollars;
	private BigDecimal financeRental;
	private BigDecimal serviceRental;
	private BigDecimal visionFinanceRental;
	private BigDecimal visionServiceRental;
	private BigDecimal visionTotalRental;
	private BigDecimal rateSheetRental;
	
	public BigDecimal getTotalRental() {
	    if(totalRental != null)
		totalRental = totalRental.setScale(2, BigDecimal.ROUND_HALF_UP);
		return totalRental;
	}
	public void setTotalRental(BigDecimal totalRental) {
		this.totalRental = totalRental;
	}
	public BigDecimal getProfitPercentage() {
	    if(profitPercentage != null)
		profitPercentage = profitPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
		return profitPercentage;
	}
	public void setProfitPercentage(BigDecimal profitPercentage) {
		this.profitPercentage = profitPercentage;
	}
	public BigDecimal getProfitDollars() {
	    if(profitDollars != null)
		profitDollars = profitDollars.setScale(2, BigDecimal.ROUND_HALF_UP);
		return profitDollars;
	}
	public void setProfitDollars(BigDecimal profitDollars) {
		this.profitDollars = profitDollars;
	}
	public BigDecimal getFinanceRental() {
	    if(financeRental != null)
		financeRental = financeRental.setScale(2, BigDecimal.ROUND_HALF_UP);
		return financeRental;
	}
	public void setFinanceRental(BigDecimal financeRental) {
		this.financeRental = financeRental;
	}
	public BigDecimal getServiceRental() {
	    if(serviceRental != null)
		serviceRental = serviceRental.setScale(2, BigDecimal.ROUND_HALF_UP);
		return serviceRental;
	}
	public void setServiceRental(BigDecimal serviceRental) {
		this.serviceRental = serviceRental;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "serviceRental--"+getServiceRental()+"--financeRental--"+getFinanceRental()+"--totalRental--"+getTotalRental() +"-profitDollars-"+getProfitDollars()+"--profitPercentage"+getProfitPercentage();
	}
	public BigDecimal getVisionFinanceRental() {
	    if(visionFinanceRental != null)
		visionFinanceRental = visionFinanceRental.setScale(2, BigDecimal.ROUND_HALF_UP);		
	    return visionFinanceRental;
	}
	public void setVisionFinanceRental(BigDecimal visionFinanceRental) {
	    this.visionFinanceRental = visionFinanceRental;
	}
	public BigDecimal getVisionServiceRental() {
	    if(visionServiceRental != null)
		visionServiceRental = visionServiceRental.setScale(2, BigDecimal.ROUND_HALF_UP);		
	    return visionServiceRental;
	}
	public void setVisionServiceRental(BigDecimal visionServiceRental) {
	    this.visionServiceRental = visionServiceRental;
	}
	public BigDecimal getVisionTotalRental() {
	    if(visionTotalRental != null)
	   	 visionTotalRental = visionTotalRental.setScale(2, BigDecimal.ROUND_HALF_UP);		
	    return visionTotalRental;
	}
	public void setVisionTotalRental(BigDecimal visionTotalRental) {
	    this.visionTotalRental = visionTotalRental;   
	}

	public BigDecimal getRateSheetRental() {
	    if(rateSheetRental != null)
		rateSheetRental = rateSheetRental.setScale(2, BigDecimal.ROUND_HALF_UP);		
	    return rateSheetRental;
	}
	public void setRateSheetRental(BigDecimal rateSheetRental) {
	    this.rateSheetRental = rateSheetRental;  
	}
}
