package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import com.mikealbert.data.entity.QuotationCapitalElement;

public  class CapitalCostEntryVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private QuotationCapitalElement quotationCapitalElement;		
		private BigDecimal value;
		private BigDecimal originalValue;
		private boolean finalClientCostFlag;
		

		public CapitalCostEntryVO(){}

		public QuotationCapitalElement getQuotationCapitalElement() {
			return quotationCapitalElement;
		}

		public void setQuotationCapitalElement(QuotationCapitalElement quotationCapitalElement) {
			this.quotationCapitalElement = quotationCapitalElement;
		}

		public BigDecimal getValue() {
			return value;
		}
		

		public void setValue(BigDecimal value) {
			this.value = value;
		}
		
		public BigDecimal getOriginalValue() {
		    return originalValue;
		}

		public void setOriginalValue(BigDecimal originalValue) {
		    this.originalValue = originalValue;
		}
		@Override
		public String toString() {
		    return "CapitalCostEntryVO [quotationCapitalElement=" + quotationCapitalElement + ", value=" + value + ", originalValue=" + originalValue + "]";
		}

		public boolean isFinalClientCostFlag() {
			return finalClientCostFlag;
		}

		public void setFinalClientCostFlag(boolean finalClientCostFlag) {
			this.finalClientCostFlag = finalClientCostFlag;
		}
}