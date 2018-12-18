package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public  class CapitalCostOverviewRowVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String name;
		private String type;
		private BigDecimal standardOrderDealCost;
		private BigDecimal standardOrderCustomerCost;
		private BigDecimal firstQuoteDealCost;
		private BigDecimal firstQuoteCustomerCost;
		private BigDecimal invoiceCost;
		private boolean isAccessoryData;
		private BigDecimal finalizedQuoteDealCost;
		private BigDecimal finalizedQuoteCustomerCost;
		private Boolean footer;

		private BigDecimal	rechargeAmt;
		private Boolean hasReclaim;
		
		private String	poNumber;
		private Date	poOrderDate;
		private	String	accountInfo;
		
		public CapitalCostOverviewRowVO(){}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BigDecimal getStandardOrderDealCost() {
			return standardOrderDealCost;
		}

		public void setStandardOrderDealCost(BigDecimal standardOrderDealCost) {
			this.standardOrderDealCost = standardOrderDealCost;
		}

		public BigDecimal getStandardOrderCustomerCost() {
			return standardOrderCustomerCost;
		}

		public void setStandardOrderCustomerCost(BigDecimal standardOrderCustomerCost) {
			this.standardOrderCustomerCost = standardOrderCustomerCost;
		}

		public BigDecimal getFirstQuoteDealCost() {
			return firstQuoteDealCost;
		}

		public void setFirstQuoteDealCost(BigDecimal firstQuoteDealCost) {
			this.firstQuoteDealCost = firstQuoteDealCost;
		}

		public BigDecimal getFirstQuoteCustomerCost() {
			return firstQuoteCustomerCost;
		}

		public void setFirstQuoteCustomerCost(BigDecimal firstQuoteCustomerCost) {
			this.firstQuoteCustomerCost = firstQuoteCustomerCost;
		}

		public BigDecimal getInvoiceCost() {
			return invoiceCost;
		}

		public void setInvoiceCost(BigDecimal invoiceCost) {
			this.invoiceCost = invoiceCost;
		}

		public BigDecimal getFinalizedQuoteDealCost() {
			return finalizedQuoteDealCost;
		}

		public void setFinalizedQuoteDealCost(BigDecimal finalizedQuoteDealCost) {
			this.finalizedQuoteDealCost = finalizedQuoteDealCost;
		}

		public BigDecimal getFinalizedQuoteCustomerCost() {
			return finalizedQuoteCustomerCost;
		}

		public void setFinalizedQuoteCustomerCost(BigDecimal finalizedQuoteCustomerCost) {
			this.finalizedQuoteCustomerCost = finalizedQuoteCustomerCost;
		}

		public Boolean getIsFooter() {
			return footer;
		}

		public void setFooter(Boolean footer) {
			this.footer = footer;
		}
		

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public BigDecimal getRechargeAmt() {
			return rechargeAmt;
		}

		public void setRechargeAmt(BigDecimal rechargeAmt) {
			this.rechargeAmt = rechargeAmt;
		}

		public Boolean getHasReclaim() {
			return hasReclaim;
		}

		public void setHasReclaim(Boolean hasReclaim) {
			this.hasReclaim = hasReclaim;
		}

		public boolean getIsAccessoryData() {
			return isAccessoryData;
		}

		public void setAccessoryData(boolean isAccessoryData) {
			this.isAccessoryData = isAccessoryData;
		}

		@Override
		public String toString() {
			return "CapitalCostOverviewRowVO [name=" + name
					+ ", standardOrderDealCost=" + standardOrderDealCost
					+ ", standardOrderCustomerCost="
					+ standardOrderCustomerCost + ", firstQuoteDealCost="
					+ firstQuoteDealCost + ", firstQuoteCustomerCost="
					+ firstQuoteCustomerCost + ", invoiceCost=" + invoiceCost
				+ ", finalizedQuoteDealCost=" + finalizedQuoteDealCost
					+ ", finalizedQuoteCustomerCost="
					+ finalizedQuoteCustomerCost + ", rechargeAmt="
					+ rechargeAmt + ", hasReclaim=" + hasReclaim + "]";
		}

		public String getPoNumber() {
			return poNumber;
		}

		public void setPoNumber(String poNumber) {
			this.poNumber = poNumber;
		}

		public Date getPoOrderDate() {
			return poOrderDate;
		}

		public void setPoOrderDate(Date poOrderDate) {
			this.poOrderDate = poOrderDate;
		}

		public String getAccountInfo() {
			return accountInfo;
		}

		public void setAccountInfo(String accountInfo) {
			this.accountInfo = accountInfo;
		}	
		

}
