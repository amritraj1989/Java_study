package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public  class ServicesLeaseRateByPeriodVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String description;
		private BigDecimal monthlyCost;
	    private Long fromPeriod;
	    private Long toPeriod;
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public BigDecimal getMonthlyCost() {
			return monthlyCost;
		}
		public void setMonthlyCost(BigDecimal monthlyCost) {
			this.monthlyCost = monthlyCost;
		}
		public Long getFromPeriod() {
			return fromPeriod;
		}
		public void setFromPeriod(Long fromPeriod) {
			this.fromPeriod = fromPeriod;
		}
		public Long getToPeriod() {
			return toPeriod;
		}
		public void setToPeriod(Long toPeriod) {
			this.toPeriod = toPeriod;
		}
}
