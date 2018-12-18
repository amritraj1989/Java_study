package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public  class CapitalCostEquipmentRowVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String name;
		private String type;
		private BigDecimal standardOrderDealCost;
		private BigDecimal firstQuoteDealCost;
		private BigDecimal invoiceCost;
		private BigDecimal finalizedQuoteDealCost;
		private	BigDecimal rechargeAmt;
		
		public CapitalCostEquipmentRowVO(){}

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

		public BigDecimal getFirstQuoteDealCost() {
			return firstQuoteDealCost;
		}

		public void setFirstQuoteDealCost(BigDecimal firstQuoteDealCost) {
			this.firstQuoteDealCost = firstQuoteDealCost;
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
		

}
