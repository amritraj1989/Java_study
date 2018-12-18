package com.mikealbert.service.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public  class QuoteCostElementVO implements Serializable {
		
	private static final long serialVersionUID = 1L;

		private String elementName;
		private Long elementId;
		private String elementcode;
		private String elementGroupName;
		private int  elementGroupOrder;
		private int elementOrderInGroup;
		private boolean isCapitalElements;
		private boolean isReclaim;
		private boolean isModelAccessories;
		private boolean isDealerAccessories;
		private String onInvoice;		
		private String  lfMarginOnly;
		private String  quoteConcealed;
		private String  quoteCapital;
		private String  rechargable;
		
		
		
		private BigDecimal dealCost;
		private BigDecimal customerCost;
		
		
		private	String	elementType;
		private boolean	groupCostElementData = false;
		private	BigDecimal	rechargeAmt;
		private Long quotationCapitalElementId;
		
		private String	poNumber;
		private Date	poOrderDate;
		private Long poRevNo ;
		private	String	accountInfo;
		
		public String getElementType() {
			return elementType;
		}

		public void setElementType(String elementType) {
			this.elementType = elementType;
		}

		public QuoteCostElementVO(){}
		
		public QuoteCostElementVO(String  elementName , int elementOrderInGroup ){			
			this.elementName = elementName;
			this.elementOrderInGroup = elementOrderInGroup;
			
		}
		public QuoteCostElementVO(String  elementName ,BigDecimal dealCost, BigDecimal customerCost){
			
			this.elementName = elementName;
			this.setDealCost(dealCost);
			this.setCustomerCost(customerCost);
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

		public BigDecimal getDealCost() {
			return dealCost;
		}

		public void setDealCost(BigDecimal dealCost) {
			this.dealCost = dealCost;
		}

		public BigDecimal getCustomerCost() {
			return customerCost;
		}

		public void setCustomerCost(BigDecimal customerCost) {
			this.customerCost = customerCost;
		}

		public String getQuoteConcealed() {
			return quoteConcealed;
		}

		public void setQuoteConcealed(String quoteConcealed) {
			this.quoteConcealed = quoteConcealed;
		}

		public BigDecimal getRechargeAmt() {
			return rechargeAmt;
		}

		public void setRechargeAmt(BigDecimal rechargeAmt) {
			this.rechargeAmt = rechargeAmt;
		}

		public Long getQuotationCapitalElementId() {
			return quotationCapitalElementId;
		}

		public void setQuotationCapitalElementId(Long quotationCapitalElementId) {
			this.quotationCapitalElementId = quotationCapitalElementId;
		}

		public boolean isDealerAccessories() {
			return isDealerAccessories;
		}

		public void setDealerAccessories(boolean isDealerAccessories) {
			this.isDealerAccessories = isDealerAccessories;
		}

		public boolean isModelAccessories() {
			return isModelAccessories;
		}

		public void setModelAccessories(boolean isModelAccessories) {
			this.isModelAccessories = isModelAccessories;
		}

		
		@Override
		public String toString() {
			return "QuoteCostElementVO [elementName=" + elementName
					+ ", elementGroupName=" + elementGroupName
					+ ", isModelAccessories=" + isModelAccessories
					+ ", isDealerAccessories=" + isDealerAccessories + "]";
		}

		public boolean isReclaim() {
			return isReclaim;
		}

		public void setReclaim(boolean isReclaim) {
			this.isReclaim = isReclaim;
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

		public Long getPoRevNo() {
			return poRevNo;
		}

		public void setPoRevNo(Long poRevNo) {
			this.poRevNo = poRevNo;
		}

		public String getQuoteCapital() {
			return quoteCapital;
		}

		public void setQuoteCapital(String quoteCapital) {
			this.quoteCapital = quoteCapital;
		}

		public String getRechargable() {
			return rechargable;
		}

		public void setRechargable(String rechargable) {
			this.rechargable = rechargable;
		}
		
	
}
