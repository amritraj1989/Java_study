package com.mikealbert.service.vo;

import java.io.Serializable;

import com.mikealbert.data.entity.QuotationModel;

public  class CapitalCostModeValuesVO implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String mode;
		private QuotationModel standardOrderQuoteModel;
		private QuotationModel firstQuoteModel;
		private QuotationModel finalQuoteModel;
		private Boolean isStockOrder;
		private String unitNo;
		
		public CapitalCostModeValuesVO(){}

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

	
		public QuotationModel getStandardOrderQuoteModel() {
			return standardOrderQuoteModel;
		}

		public void setStandardOrderQuoteModel(QuotationModel standardOrderQuoteModel) {
			this.standardOrderQuoteModel = standardOrderQuoteModel;
		}

		public QuotationModel getFirstQuoteModel() {
			return firstQuoteModel;
		}

		public void setFirstQuoteModel(QuotationModel firstQuoteModel) {
			this.firstQuoteModel = firstQuoteModel;
		}

		public QuotationModel getFinalQuoteModel() {
			return finalQuoteModel;
		}

		public void setFinalQuoteModel(QuotationModel finalQuoteModel) {
			this.finalQuoteModel = finalQuoteModel;
		}

		public Boolean getIsStockOrder() {
			return isStockOrder;
		}

		public void setIsStockOrder(Boolean isStockOrder) {
			this.isStockOrder = isStockOrder;
		}

		public String getUnitNo() {
			return unitNo;
		}

		public void setUnitNo(String unitNo) {
			this.unitNo = unitNo;
		}


}
