package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.util.MALUtilities;

public class QuoteOverviewVO implements Serializable {

	private static final long serialVersionUID = -6751126470104082189L;

	private String accountCode;
	private String accountName;
	private String unitNo;
	private String unitNoReplacement;
	private String quoteProfileDesc;
	private String unitDesc;
	private String unitDescReplacement;
	private String excessMileBand;
	private String costTreatment;
	private String interestTreatment;
	private String quote;
	private String productDesc;
	private String mileageProgramDesc;
	private BigDecimal dealCost ;
	private BigDecimal customerCost;
	private Long term;
	private Long distance;
	private BigDecimal equipmentResidual;
	private BigDecimal baseResidual;
	private BigDecimal baseResidualToCompare;
	private BigDecimal totalResidual;
	private BigDecimal minimumLeaseRate;
	private BigDecimal actualLeaseRate;
	private BigDecimal actualLeaseRateToCompare;
	private BigDecimal serviceElementRate;	
	private String actualIrr;
	private BigDecimal actualIrrValue;
	private BigDecimal actualIrrToCompare;
	
	private BigDecimal baseIrr; //huddle
	private BigDecimal profitAdj;
	private	String	irrApprovedUser;
	private Date 	irrApprovedDate;
	private String irrApprovedLimit;	
	private BigDecimal irrApprovedMinLimit;
	private boolean disableApprovedMinIrr;
	private final int DECIMAL_DIG_PRECISION_THREE = 3;
	private BigDecimal grdDeliveryRechargeAmount;
	private String grdDeliveryRechargeAmountLabel;
	private boolean	disableGrdDelivery = false;
	private	BigDecimal capitalContribution;
	private	BigDecimal capitalContributionToCompare;
	private String	productName;
	private String	orderType;
	private String	plateTypeCodeDescription;
	private String	gradeGroupDescription;
	
	private BigDecimal invoiceAdjustment;
	private BigDecimal	invoiceAdjustmentToCompare;
	
	private boolean	disableInvoiceAdjustment = false;
	
	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getUnitNoReplacement() {
		return unitNoReplacement;
	}

	public void setUnitNoReplacement(String unitNoReplacement) {
		this.unitNoReplacement = unitNoReplacement;
	}

	public String getExcessMileBand() {
		return excessMileBand;
	}

	public void setExcessMileBand(String excessMileBand) {
		this.excessMileBand = excessMileBand;
	}

	public String getQuoteProfileDesc() {
		return quoteProfileDesc;
	}

	public void setQuoteProfileDesc(String quoteProfileDesc) {
		this.quoteProfileDesc = quoteProfileDesc;
	}

	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public String getUnitDescReplacement() {
		return unitDescReplacement;
	}

	public void setUnitDescReplacement(String unitDescReplacement) {
		this.unitDesc = unitDescReplacement;
	}

	public String getCostTreatment() {
		return costTreatment;
	}

	public void setCostTreatment(String costTreatment) {
		this.costTreatment = costTreatment;
	}

	public String getInterestTreatment() {
		return interestTreatment;
	}

	public void setInterestTreatment(String interestTreatment) {
		this.interestTreatment = interestTreatment;
	}


	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getMileageProgramDesc() {
		return mileageProgramDesc;
	}

	public void setMileageProgramDesc(String mileageProgramDesc) {
		this.mileageProgramDesc = mileageProgramDesc;
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
	
	public Long getTerm() {
		return term;
	}

	public void setTerm(Long term) {
		this.term = term;
	}

	public Long getDistance() {
		return distance;
	}

	public void setDistance(Long distance) {
		this.distance = distance;
	}


	public BigDecimal getMinimumLeaseRate() {
		return minimumLeaseRate;
	}

	public void setMinimumLeaseRate(BigDecimal minimumLeaseRate) {
		this.minimumLeaseRate = minimumLeaseRate;
	}

	public BigDecimal getActualLeaseRate() {
		return actualLeaseRate;
	}

	public void setActualLeaseRate(BigDecimal actualLeaseRate) {
		this.actualLeaseRate = actualLeaseRate;
	}

	public BigDecimal getServiceElementRate() {
		return serviceElementRate;
	}

	public void setServiceElementRate(BigDecimal serviceElementRate) {
		this.serviceElementRate = serviceElementRate;
	}

	public Double getMonthlyPayment() {
	   double totalAmount = 00.00;
	   if(serviceElementRate != null){
	       totalAmount = serviceElementRate.doubleValue();
	   }
	   if(actualLeaseRate != null  )
	       totalAmount +=  actualLeaseRate.doubleValue();
	   return totalAmount;
	}
	
	public BigDecimal getActualIrrValue() {
		/*if(!MALUtilities.isEmpty(actualIrr )){
			return new BigDecimal(actualIrr.replaceAll("%", ""));
		}else{
			return null;
		}*/
		return actualIrrValue;
	    
	
	}
	public String getActualIrr() {
		return actualIrr ;
	}

	public void setActualIrr(String actualIrr) {
		this.actualIrr = actualIrr;
	}

	
	public BigDecimal getEquipmentResidual() {
		return equipmentResidual;
	}

	public void setEquipmentResidual(BigDecimal equipmentResidual) {
		this.equipmentResidual = equipmentResidual;
	}

	public BigDecimal getBaseResidual() {
		return baseResidual;
	}

	public void setBaseResidual(BigDecimal baseResidual) {
		this.baseResidual = baseResidual;
	}

	public BigDecimal getTotalResidual() {
	    
	    totalResidual =  BigDecimal.valueOf(0.00);
	    
	    if(baseResidual != null){
		totalResidual = totalResidual.add(baseResidual);
	    }	    
	    if(equipmentResidual != null){
		totalResidual =  totalResidual.add(equipmentResidual);
	    }
	   
	    return totalResidual;
	}	

	public BigDecimal getBaseIrr() {
	    return baseIrr;
	}

	public void setBaseIrr(BigDecimal baseIrr) {
	    this.baseIrr = baseIrr;
	}

	public BigDecimal getProfitAdj() {
	    return profitAdj;
	}

	public void setProfitAdj(BigDecimal profitAdj) {
	    this.profitAdj = profitAdj;
	}
	public BigDecimal getMinimumIrr() {
	    
	    BigDecimal  minimumIrr = new BigDecimal("0.000");	
	    if(baseIrr != null){
		minimumIrr = minimumIrr.add(baseIrr);
	    }	    
	    if(profitAdj != null){
		minimumIrr =  minimumIrr.add(profitAdj);
	    }
	    
	    CommonCalculations.getRoundedValue(minimumIrr, DECIMAL_DIG_PRECISION_THREE);
	    
	    return minimumIrr;
	}

	public BigDecimal getBaseResidualToCompare() {
		return baseResidualToCompare;
	}

	public void setBaseResidualToCompare(BigDecimal baseResidualToCompare) {
		this.baseResidualToCompare = baseResidualToCompare;
	}

	public BigDecimal getActualLeaseRateToCompare() {
		return actualLeaseRateToCompare;
	}

	public void setActualLeaseRateToCompare(BigDecimal actualLeaseRateToCompare) {
		this.actualLeaseRateToCompare = actualLeaseRateToCompare;
	}

	public BigDecimal getActualIrrToCompare() {
		return actualIrrToCompare;
	}

	public void setActualIrrToCompare(BigDecimal actualIrrToCompare) {
		this.actualIrrToCompare = actualIrrToCompare;
	}

	public String getIrrApprovedUser() {
		return irrApprovedUser;
	}

	public void setIrrApprovedUser(String irrApprovedUser) {
		this.irrApprovedUser = irrApprovedUser;
	}

	public Date getIrrApprovedDate() {
		return irrApprovedDate;
	}

	public void setIrrApprovedDate(Date irrApprovedDate) {
		this.irrApprovedDate = irrApprovedDate;
	}

	public String getIrrApprovedLimit() {
		return irrApprovedLimit ;
	}

	public void setIrrApprovedLimit(String irrApprovedLimit) {
		this.irrApprovedLimit = irrApprovedLimit;
	}
	public BigDecimal getIrrApprovedLimitValue() {	    
	    return MALUtilities.isEmpty(irrApprovedLimit ) ? null:new BigDecimal( irrApprovedLimit.replaceAll("%", ""));
	
	}

	public BigDecimal getIrrApprovedMinLimit() {
	    return irrApprovedMinLimit;
	}

	public void setIrrApprovedMinLimit(BigDecimal irrApprovedMinLimit) {
	    this.irrApprovedMinLimit = irrApprovedMinLimit;
	}

	public boolean isDisableApprovedMinIrr() {
	    this.disableApprovedMinIrr = false;
	    if(irrApprovedMinLimit == null ) {
		disableApprovedMinIrr =  true;
	    }
	     
	    return disableApprovedMinIrr;
	}

	public void setDisableApprovedMinIrr(boolean disableApprovedMinIrr) {
	    this.disableApprovedMinIrr = disableApprovedMinIrr;
	}

	public void setActualIrrValue(BigDecimal actualIrrValue) {
		this.actualIrrValue = actualIrrValue;
	}

	public BigDecimal getGrdDeliveryRechargeAmount() {
		return grdDeliveryRechargeAmount;
	}

	public void setGrdDeliveryRechargeAmount(BigDecimal grdDeliveryRechargeAmount) {
		this.grdDeliveryRechargeAmount = grdDeliveryRechargeAmount;
	}

	public boolean isDisableGrdDelivery() {
		return disableGrdDelivery;
	}

	public void setDisableGrdDelivery(boolean disableGrdDelivery) {
		this.disableGrdDelivery = disableGrdDelivery;
	}

	public BigDecimal getCapitalContributionToCompare() {
	    return capitalContributionToCompare;
	}

	public void setCapitalContributionToCompare(BigDecimal capitalContributionToCompare) {
	    this.capitalContributionToCompare = capitalContributionToCompare;
	}

	public BigDecimal getCapitalContribution() {
	    return capitalContribution;
	}

	public void setCapitalContribution(BigDecimal capitalContribution) {
	    this.capitalContribution = capitalContribution;
	}

	public String getGrdDeliveryRechargeAmountLabel() {
	    return grdDeliveryRechargeAmountLabel;
	}

	public void setGrdDeliveryRechargeAmountLabel(String grdDeliveryRechargeAmountLabel) {
	    this.grdDeliveryRechargeAmountLabel = grdDeliveryRechargeAmountLabel;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPlateTypeCodeDescription() {
		return plateTypeCodeDescription;
	}

	public void setPlateTypeCodeDescription(String plateTypeCodeDescription) {
		this.plateTypeCodeDescription = plateTypeCodeDescription;
	}

	public String getGradeGroupDescription() {
		return gradeGroupDescription;
	}

	public void setGradeGroupDescription(String gradeGroupDescription) {
		this.gradeGroupDescription = gradeGroupDescription;
	}

	public BigDecimal getInvoiceAdjustment() {
		return invoiceAdjustment;
	}

	public void setInvoiceAdjustment(BigDecimal invoiceAdjustment) {
		this.invoiceAdjustment = invoiceAdjustment;
	}

	public BigDecimal getInvoiceAdjustmentToCompare() {
		return invoiceAdjustmentToCompare;
	}

	public void setInvoiceAdjustmentToCompare(BigDecimal invoiceAdjustmentToCompare) {
		this.invoiceAdjustmentToCompare = invoiceAdjustmentToCompare;
	}

	public boolean isDisableInvoiceAdjustment() {
		return disableInvoiceAdjustment;
	}

	public void setDisableInvoiceAdjustment(boolean disableInvoiceAdjustment) {
		this.disableInvoiceAdjustment = disableInvoiceAdjustment;
	}

	
	
}
