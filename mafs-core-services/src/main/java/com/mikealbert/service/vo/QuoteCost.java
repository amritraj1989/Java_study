package com.mikealbert.service.vo;

import java.math.BigDecimal;

public class QuoteCost {

	private BigDecimal dealCost = BigDecimal.ZERO;
	private BigDecimal customerCost= BigDecimal.ZERO;

	public BigDecimal getDealCost() {
		return dealCost;
	}

	public void setDealCost(BigDecimal dealCost) {
		this.dealCost = dealCost;
	}

	public BigDecimal getCustomerCost() {
		return customerCost;
	}

	public void setCustomerCost(BigDecimal cleintCost) {
		this.customerCost = cleintCost;
	}

}
