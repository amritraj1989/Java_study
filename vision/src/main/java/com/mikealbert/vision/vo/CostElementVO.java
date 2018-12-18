package com.mikealbert.vision.vo;

import java.io.Serializable;

import com.mikealbert.util.MALUtilities;

public  class CostElementVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String elementName;
		private Long elementId;
		private String elementcode;
		private String elementGroupName;
		private int  elementGroupOrder;
		private int elementOrderInGroup;
		private boolean hasDetailElements;
		private boolean isCapitalElements;
		private String onInvoice;		
		private String  lfMarginOnly;

		private Double originalOrderDeal = new Double(0);
		private Double originalOrderCustomer = new Double(0);
		
		private Double acceptedQuoteDeal = new Double(0);
		private Double acceptedQuoteCustomer = new Double(0);
		
		private Double invoiceAmount = new Double(0);
		
		private Double finalizedQuoteDeal = new Double(0);
		private Double finalizedQuoteCustomer = new Double(0);
		
		private	String	elementType;
		private boolean	groupCostElementData = false;;
		
		public String getElementType() {
			return elementType;
		}

		public void setElementType(String elementType) {
			this.elementType = elementType;
		}

		public CostElementVO(){}
		
		public CostElementVO(String  elementName , int elementOrderInGroup ){			
			this.elementName = elementName;
			this.elementOrderInGroup = elementOrderInGroup;
			
		}
		public CostElementVO(String  elementName ,Double originalOrderDeal , Double originalOrderCustomer , Double acceptedQuoteDeal ,Double acceptedQuoteCustomer ,
				Double invoiceAmount ,Double finalizedQuoteDeal , Double finalizedQuoteCustomer ){
			
			this.elementName = elementName;
			this.originalOrderDeal = originalOrderDeal;
			this.originalOrderCustomer = originalOrderCustomer;
			this.acceptedQuoteDeal = acceptedQuoteDeal;
			this.acceptedQuoteCustomer = acceptedQuoteCustomer;
			this.invoiceAmount = invoiceAmount;
			this.finalizedQuoteDeal = finalizedQuoteDeal;
			this.finalizedQuoteCustomer = finalizedQuoteCustomer;
			
		}

		public String getElementName() {
			return elementName;
		}
		public void setElementName(String elementName) {
			this.elementName = elementName;
		}
		public Long getElementId() {
			return elementId;
		}
		public void setElementId(Long elementId) {
			this.elementId = elementId;
		}
		public Double getOriginalOrderDeal() {
			return MALUtilities.round(originalOrderDeal,2);
		}
		public String getOriginalOrderDealAsString() {
			return String.format("%.2f", MALUtilities.round(originalOrderDeal,2));
		}
		public void setOriginalOrderDeal(Double originalOrderDeal) {
			this.originalOrderDeal = originalOrderDeal;
		}
		public Double getOriginalOrderCustomer() {
			return MALUtilities.round(originalOrderCustomer,2);
		}
		public String getOriginalOrderCustomerAsString() {
			return String.format("%.2f", MALUtilities.round(originalOrderCustomer,2));
		}
		public void setOriginalOrderCustomer(Double originalOrderCustomer) {
			this.originalOrderCustomer = originalOrderCustomer;
		}
		public Double getAcceptedQuoteDeal() {
			return MALUtilities.round(acceptedQuoteDeal,2);
		}
		public String getAcceptedQuoteDealAsString() {
			return String.format("%.2f", MALUtilities.round(acceptedQuoteDeal,2));
		}
		public void setAcceptedQuoteDeal(Double acceptedQuoteDeal) {
			this.acceptedQuoteDeal = acceptedQuoteDeal;
		}
		public Double getAcceptedQuoteCustomer() {
			return MALUtilities.round(acceptedQuoteCustomer,2);	
		}
		public String getAcceptedQuoteCustomerAsString() {
			return String.format("%.2f", MALUtilities.round(acceptedQuoteCustomer,2));	
		}
		public void setAcceptedQuoteCustomer(Double acceptedQuoteCustomer) {
			this.acceptedQuoteCustomer = acceptedQuoteCustomer;
		}
		public Double getInvoiceAmount() {
			return MALUtilities.round(invoiceAmount,2);			
		}
		public String getInvoiceAmountAsString() {
			return String.format("%.2f", MALUtilities.round(invoiceAmount,2));			
		}
		public void setInvoiceAmount(Double invoiceAmount) {
			this.invoiceAmount = invoiceAmount;
		}
		public Double getFinalizedQuoteDeal() {
			return MALUtilities.round(finalizedQuoteDeal,2);			
		}
		public String getFinalizedQuoteDealAsString() {
			return String.format("%.2f", MALUtilities.round(finalizedQuoteDeal,2));			
		}
		public void setFinalizedQuoteDeal(Double finalizedQuoteDeal) {
			this.finalizedQuoteDeal = finalizedQuoteDeal;
		}
		public Double getFinalizedQuoteCustomer() {
			return MALUtilities.round(finalizedQuoteCustomer,2);			
		}
		public String getFinalizedQuoteCustomerAsString() {
			return String.format("%.2f", MALUtilities.round(finalizedQuoteCustomer,2));			
		}
		public void setFinalizedQuoteCustomer(Double finalizedQuoteCustomer) {
			this.finalizedQuoteCustomer = finalizedQuoteCustomer;
		}

		public boolean isHasDetailElements() {
			return hasDetailElements;
		}

		public void setHasDetailElements(boolean hasDetailElements) {
			this.hasDetailElements = hasDetailElements;
		}

		public String getElementGroupName() {
			return elementGroupName;
		}

		public void setElementGroupName(String elementGroupName) {
			this.elementGroupName = elementGroupName;
		}
		
		public int getElementGroupOrder() {
			return elementGroupOrder;
		}

		public void setElementGroupOrder(int elementGroupOrder) {
			this.elementGroupOrder = elementGroupOrder;
		}
		
		public int getElementOrderInGroup() {
			return elementOrderInGroup;
		}

		public void setElementOrderInGroup(int elementOrderInGroup) {
			this.elementOrderInGroup = elementOrderInGroup;
		}
	
		public boolean isCapitalElements() {
			return isCapitalElements;
		}

		public void setCapitalElements(boolean isCapitalElements) {
			this.isCapitalElements = isCapitalElements;
		}
		
		public String getOnInvoice() {
			return onInvoice;
		}

		public void setOnInvoice(String onInvoice) {
			this.onInvoice = onInvoice;
		}
	

		public String getLfMarginOnly() {
			return lfMarginOnly;
		}

		public void setLfMarginOnly(String lfMarginOnly) {
			this.lfMarginOnly = lfMarginOnly;
		}

		public String getElementcode() {
			return elementcode;
		}

		public void setElementcode(String elementcode) {
			this.elementcode = elementcode;
		}

		public boolean isGroupCostElementData() {
			return groupCostElementData;
		}

		public void setGroupCostElementData(boolean groupCostElementData) {
			this.groupCostElementData = groupCostElementData;
		}
		
	
}
