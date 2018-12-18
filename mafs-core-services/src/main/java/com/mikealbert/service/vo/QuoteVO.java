package com.mikealbert.service.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.QuotationModel;

public  class QuoteVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private QuotationModel quotationModel;
		private BigDecimal totalCostToPlaceInServiceDeal;
		private BigDecimal totalCostToPlaceInServiceCustomer;
		private List<QuoteCostElementVO> costElements;
		private Long invoiceQmdId;

		public QuoteVO(){}

		public QuotationModel getQuotationModel() {
			return quotationModel;
		}

		public void setQuotationModel(QuotationModel quotationModel) {
			this.quotationModel = quotationModel;
		}

		public BigDecimal getTotalCostToPlaceInServiceDeal() {
			BigDecimal sum = new BigDecimal(0);
			for (QuoteCostElementVO costElement : this.costElements) {
				 sum = sum.add(costElement.getDealCost() != null ? costElement.getDealCost(): new BigDecimal(0));
			}
			setTotalCostToPlaceInServiceDeal(sum);

			return totalCostToPlaceInServiceDeal;
		}

		public void setTotalCostToPlaceInServiceDeal(BigDecimal totalCostToPlaceInServiceDeal) {
			this.totalCostToPlaceInServiceDeal = totalCostToPlaceInServiceDeal;
		}

		public BigDecimal getTotalCostToPlaceInServiceCustomer() {
			BigDecimal sum = new BigDecimal(0);
			for (QuoteCostElementVO costElement : this.costElements) {
				 sum = sum.add(costElement.getCustomerCost() != null ? costElement.getCustomerCost(): new BigDecimal(0));
			}
			setTotalCostToPlaceInServiceCustomer(sum);

			return totalCostToPlaceInServiceCustomer;
		}

		public void setTotalCostToPlaceInServiceCustomer(
				BigDecimal totalCostToPlaceInServiceCustomer) {
			this.totalCostToPlaceInServiceCustomer = totalCostToPlaceInServiceCustomer;
		}

		public List<QuoteCostElementVO> getCostElements() {
			return costElements;
		}

		public void setCostElements(List<QuoteCostElementVO> costElements) {
			this.costElements = costElements;
		}

		public Long getInvoiceQmdId() {
			return invoiceQmdId;
		}

		public void setInvoiceQmdId(Long invoiceQmdId) {
			this.invoiceQmdId = invoiceQmdId;
		}



	
}
