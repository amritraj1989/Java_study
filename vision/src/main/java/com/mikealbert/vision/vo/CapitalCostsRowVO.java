package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.service.enumeration.NonCapitalElementEnum;
import com.mikealbert.util.MALUtilities;

public  class CapitalCostsRowVO implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long id;
		private String name;
		private String type;
		private String code;
		private BigDecimal standardOrderDealCost;
		private BigDecimal standardOrderCustomerCost;
		private BigDecimal firstQuoteDealCost;
		private BigDecimal firstQuoteCustomerCost;
		private BigDecimal invoiceCost;
		private boolean isAccessoryData;
		private BigDecimal finalizedQuoteDealCost;
		private BigDecimal finalizedQuoteCustomerCost;
		private boolean footer;

		private BigDecimal	rechargeAmt;
		private boolean hasReclaim;
		private boolean possibleReclaim;
		private Long reclaimLineId;

		private String	poNumber;
		private Long poRevNo ;
		private Date	poOrderDate;
		private	String	accountInfo;
		private boolean clientCostAdjustmentFlag;
		private boolean fullRechargeFlag = true;
		private int elementGroup;
		
		private  boolean isBaseVehicle;
		
		private  boolean isCapitalElement;
		private  boolean isModelAccessories;	
		private  boolean isDealerAccessories;	
		private  boolean  isCapitalContribution;
		private  boolean dealCostAdjustmentFlag;


		public CapitalCostsRowVO(){}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

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

		public boolean getIsFooter() {
			return footer;
		}

		public void setFooter(boolean footer) {
			this.footer = footer;
		}


		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public BigDecimal getRechargeAmt() {
			return rechargeAmt;
		}

		public void setRechargeAmt(BigDecimal rechargeAmt) {
			this.rechargeAmt = rechargeAmt;
		}

		public boolean getHasReclaim() {
			return hasReclaim;
		}

		public void setHasReclaim(boolean hasReclaim) {
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

		public boolean isClientCostAdjustmentFlag() {
			return clientCostAdjustmentFlag;
		}

		public void setClientCostAdjustmentFlag(boolean clientCostAdjustmentFlag) {
			this.clientCostAdjustmentFlag = clientCostAdjustmentFlag;
		}

		public int getElementGroup() {
			return elementGroup;
		}

		public void setElementGroup(int elementGroup) {
			this.elementGroup = elementGroup;
		}

		public boolean isFullRechargeFlag() {
			return fullRechargeFlag;
		}

		public void setFullRechargeFlag(boolean fullRechargeFlag) {
			this.fullRechargeFlag = fullRechargeFlag;
		}

		public boolean isPossibleReclaim() {
			return possibleReclaim;
		}

		public void setPossibleReclaim(boolean possibleReclaim) {
			this.possibleReclaim = possibleReclaim;
		}

		public boolean isBaseVehicle() {
			return isBaseVehicle;
		}

		public void setBaseVehicle(boolean isBaseVehicle) {
			this.isBaseVehicle = isBaseVehicle;
		}

		public boolean isCapitalElement() {
			return isCapitalElement;
		}

		public void setCapitalElement(boolean isCapitalElement) {
			this.isCapitalElement = isCapitalElement;
		}

		public boolean isModelAccessories() {
			return isModelAccessories;
		}

		public void setModelAccessories(boolean isModelAccessories) {
			this.isModelAccessories = isModelAccessories;
		}

		public boolean isDealerAccessories() {
			return isDealerAccessories;
		}

		public void setDealerAccessories(boolean isDealerAccessories) {
			this.isDealerAccessories = isDealerAccessories;
		}

		public boolean isCapitalContribution() {
			return isCapitalContribution;
		}

		public void setCapitalContribution(boolean isCapitalContribution) {
			this.isCapitalContribution = isCapitalContribution;
		}

		public Long getReclaimLineId() {
			return reclaimLineId;
		}

		public void setReclaimLineId(Long reclaimLineId) {
			this.reclaimLineId = reclaimLineId;
		}

		public Long getPoRevNo() {
			return poRevNo;
		}

		public void setPoRevNo(Long poRevNo) {
			this.poRevNo = poRevNo;
		}
		public boolean isBase() {
			boolean isBase = false;
			
			if(!isModelAccessories 
					&& !isDealerAccessories 
					&& !isCapitalElement 
					&& !getIsFooter()					
					&& MALUtilities.isEmpty(getId())
					&& elementGroup == NonCapitalElementEnum.BASE_VEHICLE_ELEMENT.getGroupOrder()) {
				isBase = true;
			}

			return isBase;
		}
		

		public boolean isDealCostAdjustmentFlag() {
			return dealCostAdjustmentFlag;
		}

		public void setDealCostAdjustmentFlag(boolean dealCostAdjustmentFlag) {
			this.dealCostAdjustmentFlag = dealCostAdjustmentFlag;
		}
}
