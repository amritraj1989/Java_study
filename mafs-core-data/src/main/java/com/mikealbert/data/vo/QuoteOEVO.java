package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.util.MALUtilities;

public class QuoteOEVO implements Serializable {

	private static final long serialVersionUID = -6751126470104082189L;

	private String logo;
	private String oeConRevAmortizationScheduleTitle;
	private String oeConRevAmortizationScheduleSubTitle;	
	private VehicleInformationVO vehicleInformationVO;	
	private List<PeriodFinalNBVVO> finalNBVVOList;	
	private String noteSection;
	private String accountCode;
	private String accountName;
	private String unitNo;
	private String unitNoReplacement;
	private String quoteProfileDesc;
	private String unitDesc;
	private String unitDescReplacement;
	private String costTreatment;
	private String interestTreatment;
	private String quote;
	private String productDesc;
	private BigDecimal dealCost ;
	private BigDecimal dealCostToCompare;
	private BigDecimal customerCost;
	private BigDecimal customerCostToCompare;
	private Long term;
	private Long termToCompare;
	private Long distance;
	private Date contractStartDate;
	
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
	
	private Long projectedReplacementMonths;
	private String floatType;
	private String floatFrequency;
	private String interestIndex;
	private BigDecimal finalNBV;
	private BigDecimal finalNBVToCompare;
	private BigDecimal effDateNBV;
	private BigDecimal adminFactor;
	private BigDecimal depreciationFactor;
	private BigDecimal monthlyDepreciation;	
	private BigDecimal depreciationFactorToCompare;
	private BigDecimal invoiceAdjustment;
	private BigDecimal	invoiceAdjustmentToCompare;
	private BigDecimal interestTotalRate;
	private BigDecimal interestAdjustment;
	private BigDecimal interestBaseRate;
	private	BigDecimal	finalResidual;
	private	BigDecimal mainElementResidual;
	private	BigDecimal capitalContribution;
	private	BigDecimal capitalContributionToCompare;
	private BigDecimal revisedAssessment;
	private BigDecimal revisedAssessmentToCompare;
	private BigDecimal revisionInterestAdjustment;
	private BigDecimal revisionInterestAdjustmentToCompare;
	private String revisedIntAdjType;
	private String  revisedAssessmentType;
	private String revisedIntAdjTypeToCompare;
	private String  revisedAssessmentTypeToCompare;
	
	private boolean	disableAdminFactor = false;
	private boolean	disableInterestAdjustment = false;
	private boolean	disableInvoiceAdjustment = false;
	private boolean	disableGrdDelRecharge = false;
	private String	productName;
	private String	orderType;
	private String	plateTypeCodeDescription;
	private String	gradeGroupDescription;
	private boolean hideInvoiceAdjustment;
	private QuotationModel quotationModel;
	private BigDecimal mainElementAdjustedCost;
	private String driverAddress;
	private String quoteComment;
	
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

	public Long getProjectedReplacementMonths() {
		return projectedReplacementMonths;
	}

	public void setProjectedReplacementMonths(Long projectedReplacementMonths) {
		this.projectedReplacementMonths = projectedReplacementMonths;
	}

	public String getFloatType() {
		return floatType;
	}

	public void setFloatType(String floatType) {
		this.floatType = floatType;
	}

	public String getFloatFrequency() {
		return floatFrequency;
	}

	public void setFloatFrequency(String floatFrequency) {
		this.floatFrequency = floatFrequency;
	}

	public String getInterestIndex() {
		return interestIndex;
	}

	public void setInterestIndex(String interestIndex) {
		this.interestIndex = interestIndex;
	}

	public BigDecimal getFinalNBV() {
		return finalNBV;
	}

	public void setFinalNBV(BigDecimal finalNBV) {
		this.finalNBV = finalNBV;
	}

	public BigDecimal getAdminFactor() {
		return adminFactor;
	}

	public void setAdminFactor(BigDecimal adminFactor) {
		this.adminFactor = adminFactor;
	}

	public BigDecimal getDepreciationFactor() {
		return depreciationFactor;
	}

	public void setDepreciationFactor(BigDecimal depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
	}

	public BigDecimal getMonthlyDepreciation() {
		return monthlyDepreciation;
	}

	public void setMonthlyDepreciation(BigDecimal monthlyDepreciation) {
		this.monthlyDepreciation = monthlyDepreciation;
	}

	public BigDecimal getInvoiceAdjustment() {
		return invoiceAdjustment;
	}

	public void setInvoiceAdjustment(BigDecimal invoiceAdjustment) {
		this.invoiceAdjustment = invoiceAdjustment;
	}

	public BigDecimal getInterestTotalRate() {

		return CommonCalculations.getRoundedValue( interestTotalRate,3);
	}

	public void setInterestTotalRate(BigDecimal interestTotalRate) {
		this.interestTotalRate = interestTotalRate;
	}

	public BigDecimal getInterestAdjustment() {
		return interestAdjustment;
	}

	public void setInterestAdjustment(BigDecimal interestAdjustment) {
		this.interestAdjustment = interestAdjustment;
	}

	public BigDecimal getInterestBaseRate() {
		return interestBaseRate;
	}

	public void setInterestBaseRate(BigDecimal interestBaseRate) {
		this.interestBaseRate = interestBaseRate;
	}

	public BigDecimal getFinalResidual() {
		return finalResidual;
	}

	public void setFinalResidual(BigDecimal finalResidual) {
		this.finalResidual = finalResidual;
	}

	public BigDecimal getMainElementResidual() {
		return mainElementResidual;
	}

	public void setMainElementResidual(BigDecimal mainElementResidual) {
		this.mainElementResidual = mainElementResidual;
	}

	public BigDecimal getInvoiceAdjustmentToCompare() {
		return invoiceAdjustmentToCompare;
	}

	public void setInvoiceAdjustmentToCompare(BigDecimal invoiceAdjustmentToCompare) {
		this.invoiceAdjustmentToCompare = invoiceAdjustmentToCompare;
	}

	public boolean isDisableAdminFactor() {
		return disableAdminFactor;
	}

	public void setDisableAdminFactor(boolean disableAdminFactor) {
		this.disableAdminFactor = disableAdminFactor;
	}

	public boolean isDisableInterestAdjustment() {
		return disableInterestAdjustment;
	}

	public void setDisableInterestAdjustment(boolean disableInterestAdjustment) {
		this.disableInterestAdjustment = disableInterestAdjustment;
	}

	public boolean isDisableInvoiceAdjustment() {
		return disableInvoiceAdjustment;
	}

	public void setDisableInvoiceAdjustment(boolean disableInvoiceAdjustment) {
		this.disableInvoiceAdjustment = disableInvoiceAdjustment;
	}

	public boolean isDisableGrdDelRecharge() {
		return disableGrdDelRecharge;
	}

	public void setDisableGrdDelRecharge(boolean disableGrdDelRecharge) {
		this.disableGrdDelRecharge = disableGrdDelRecharge;
	}

	public BigDecimal getFinalNBVToCompare() {
		return finalNBVToCompare;
	}

	public void setFinalNBVToCompare(BigDecimal finalNBVToCompare) {
		this.finalNBVToCompare = finalNBVToCompare;
	}

	public BigDecimal getDepreciationFactorToCompare() {
		return depreciationFactorToCompare;
	}

	public void setDepreciationFactorToCompare(
			BigDecimal depreciationFactorToCompare) {
		this.depreciationFactorToCompare = depreciationFactorToCompare;
	}

	public BigDecimal getCapitalContribution() {
	    return capitalContribution;
	}

	public void setCapitalContribution(BigDecimal capitalContribution) {
	    this.capitalContribution = capitalContribution;
	}

	public BigDecimal getCapitalContributionToCompare() {
	    return capitalContributionToCompare;
	}

	public void setCapitalContributionToCompare(BigDecimal capitalContributionToCompare) {
	    this.capitalContributionToCompare = capitalContributionToCompare;
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

	public boolean isHideInvoiceAdjustment() {
		return hideInvoiceAdjustment;
	}

	public void setHideInvoiceAdjustment(boolean hideInvoiceAdjustment) {
		this.hideInvoiceAdjustment = hideInvoiceAdjustment;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public BigDecimal getRevisionInterestAdjustment() {
		return revisionInterestAdjustment;
	}

	public void setRevisionInterestAdjustment(BigDecimal revisionInterestAdjustment) {
		this.revisionInterestAdjustment = revisionInterestAdjustment;
	}

	public BigDecimal getRevisionInterestAdjustmentToCompare() {
		return revisionInterestAdjustmentToCompare;
	}

	public void setRevisionInterestAdjustmentToCompare(
			BigDecimal revisionInterestAdjustmentToCompare) {
		this.revisionInterestAdjustmentToCompare = revisionInterestAdjustmentToCompare;
	}

	public BigDecimal getRevisedAssessment() {
		return revisedAssessment;
	}

	public void setRevisedAssessment(BigDecimal revisedAssessment) {
		this.revisedAssessment = revisedAssessment;
	}

	public BigDecimal getRevisedAssessmentToCompare() {
		return revisedAssessmentToCompare;
	}

	public void setRevisedAssessmentToCompare(BigDecimal revisedAssessmentToCompare) {
		this.revisedAssessmentToCompare = revisedAssessmentToCompare;
	}

	public String getRevisedIntAdjType() {
		return revisedIntAdjType;
	}

	public void setRevisedIntAdjType(String revisedIntAdjType) {
		this.revisedIntAdjType = revisedIntAdjType;
	}

	public String getRevisedAssessmentType() {
		return revisedAssessmentType;
	}

	public void setRevisedAssessmentType(String revisedAssessmentType) {
		this.revisedAssessmentType = revisedAssessmentType;
	}

	public String getRevisedIntAdjTypeToCompare() {
		return revisedIntAdjTypeToCompare;
	}

	public void setRevisedIntAdjTypeToCompare(String revisedIntAdjTypeToCompare) {
		this.revisedIntAdjTypeToCompare = revisedIntAdjTypeToCompare;
	}

	public String getRevisedAssessmentTypeToCompare() {
		return revisedAssessmentTypeToCompare;
	}

	public void setRevisedAssessmentTypeToCompare(String revisedAssessmentTypeToCompare) {
		this.revisedAssessmentTypeToCompare = revisedAssessmentTypeToCompare;
	}

	public BigDecimal getEffDateNBV() {
		return effDateNBV;
	}

	public void setEffDateNBV(BigDecimal effDateNBV) {
		this.effDateNBV = effDateNBV;
	}

	public BigDecimal getMainElementAdjustedCost() {
		return mainElementAdjustedCost;
	}

	public void setMainElementAdjustedCost(BigDecimal mainElementAdjustedCost) {
		this.mainElementAdjustedCost = mainElementAdjustedCost;
	}

	public BigDecimal getCustomerCostToCompare() {
		return customerCostToCompare;
	}

	public void setCustomerCostToCompare(BigDecimal customerCostToCompare) {
		this.customerCostToCompare = customerCostToCompare;
	}

	public BigDecimal getDealCostToCompare() {
		return dealCostToCompare;
	}

	public void setDealCostToCompare(BigDecimal dealCostToCompare) {
		this.dealCostToCompare = dealCostToCompare;
	}

	public Long getTermToCompare() {
		return termToCompare;
	}

	public void setTermToCompare(Long termToCompare) {
		this.termToCompare = termToCompare;
	}
	public String getDriverAddress() {
		return driverAddress;
	}

	public void setDriverAddress(String driverAddress) {
		this.driverAddress = driverAddress;
	}

	public String getQuoteComment() {
		return quoteComment;
	}

	public void setQuoteComment(String quoteComment) {
		this.quoteComment = quoteComment;
	}

	public String getLogo() {
		return logo;
	}

	public VehicleInformationVO getVehicleInformationVO() {
		return vehicleInformationVO;
	}

	public void setVehicleInformationVO(VehicleInformationVO vehicleInformationVO) {
		this.vehicleInformationVO = vehicleInformationVO;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getOeConRevAmortizationScheduleTitle() {
		return oeConRevAmortizationScheduleTitle;
	}

	public void setOeConRevAmortizationScheduleTitle(
			String oeConRevAmortizationScheduleTitle) {
		this.oeConRevAmortizationScheduleTitle = oeConRevAmortizationScheduleTitle;
	}

	public String getOeConRevAmortizationScheduleSubTitle() {
		return oeConRevAmortizationScheduleSubTitle;
	}

	public void setOeConRevAmortizationScheduleSubTitle(
			String oeConRevAmortizationScheduleSubTitle) {
		this.oeConRevAmortizationScheduleSubTitle = oeConRevAmortizationScheduleSubTitle;
	}

	public List<PeriodFinalNBVVO> getFinalNBVVOList() {
		return finalNBVVOList;
	}

	public void setFinalNBVVOList(List<PeriodFinalNBVVO> finalNBVVOList) {
		this.finalNBVVOList = finalNBVVOList;
	}

	public String getNoteSection() {
		return noteSection;
	}

	public void setNoteSection(String noteSection) {
		this.noteSection = noteSection;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}	
}
