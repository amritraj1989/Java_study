package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OeConRevTermsVO {
	
	private String logo;
	private String address;	
	private String oeContractRevisionTitle;
	private String currentInterestComponent;
	private String revisionInterestComponent;
	private Long monthsRemaining;
	private Long monthsCompleted;	
	private BigDecimal inRateRevisionAssessment;
	private BigDecimal inRateRevisionInterestAdjustment;
	private BigDecimal oneTimeRevisionAssessment;
	private BigDecimal oneTimeRevisionInterestAdjustment;
	private BigDecimal totalOneTimeCharges;
	private String revisionInterestAdjustmentMonths;	
	private Date revisionStartDate;
	private Date revisionEndDate;	
	private String signatureSection;	
	private String signatureLine;
	private QuoteOEVO currentOEQuoteVO;
	private QuoteOEVO revisionOEQuoteVO;
	private ClientContactVO clientContactVO;
	private VehicleInformationVO vehicleInformationVO;	
	private List<QuotationStepStructureVO> currentQuotationStepStructureVOList;
	private List<QuotationStepStructureVO> revisionQuotationStepStructureVOList;
	private List<ServicesLeaseRateByPeriodVO> currentServicesLeaseRateByPeriodVOList;
	private List<ServicesLeaseRateByPeriodVO> revisionServicesLeaseRateByPeriodVOList;
	
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOeContractRevisionTitle() {
		return oeContractRevisionTitle;
	}
	public void setOeContractRevisionTitle(String oeContractRevisionTitle) {
		this.oeContractRevisionTitle = oeContractRevisionTitle;
	}
	public String getCurrentInterestComponent() {
		return currentInterestComponent;
	}
	public void setCurrentInterestComponent(String currentInterestComponent) {
		this.currentInterestComponent = currentInterestComponent;
	}
	public String getRevisionInterestComponent() {
		return revisionInterestComponent;
	}
	public void setRevisionInterestComponent(String revisionInterestComponent) {
		this.revisionInterestComponent = revisionInterestComponent;
	}
	public Long getMonthsRemaining() {
		return monthsRemaining;
	}
	public void setMonthsRemaining(Long monthsRemaining) {
		this.monthsRemaining = monthsRemaining;
	}
	public Long getMonthsCompleted() {
		return monthsCompleted;
	}
	public void setMonthsCompleted(Long monthsCompleted) {
		this.monthsCompleted = monthsCompleted;
	}
	public BigDecimal getInRateRevisionAssessment() {
		return inRateRevisionAssessment;
	}
	public void setInRateRevisionAssessment(BigDecimal inRateRevisionAssessment) {
		this.inRateRevisionAssessment = inRateRevisionAssessment;
	}
	public BigDecimal getInRateRevisionInterestAdjustment() {
		return inRateRevisionInterestAdjustment;
	}
	public void setInRateRevisionInterestAdjustment(BigDecimal inRateRevisionInterestAdjustment) {
		this.inRateRevisionInterestAdjustment = inRateRevisionInterestAdjustment;
	}
	public BigDecimal getOneTimeRevisionAssessment() {
		return oneTimeRevisionAssessment;
	}
	public void setOneTimeRevisionAssessment(BigDecimal oneTimeRevisionAssessment) {
		this.oneTimeRevisionAssessment = oneTimeRevisionAssessment;
	}
	public BigDecimal getOneTimeRevisionInterestAdjustment() {
		return oneTimeRevisionInterestAdjustment;
	}
	public void setOneTimeRevisionInterestAdjustment(BigDecimal oneTimeRevisionInterestAdjustment) {
		this.oneTimeRevisionInterestAdjustment = oneTimeRevisionInterestAdjustment;
	}
	public BigDecimal getTotalOneTimeCharges() {
		return totalOneTimeCharges;
	}
	public void setTotalOneTimeCharges(BigDecimal totalOneTimeCharges) {
		this.totalOneTimeCharges = totalOneTimeCharges;
	}
	public String getRevisionInterestAdjustmentMonths() {
		return revisionInterestAdjustmentMonths;
	}
	public void setRevisionInterestAdjustmentMonths(String revisionInterestAdjustmentMonths) {
		this.revisionInterestAdjustmentMonths = revisionInterestAdjustmentMonths;
	}
	public Date getRevisionStartDate() {
		return revisionStartDate;
	}
	public void setRevisionStartDate(Date revisionStartDate) {
		this.revisionStartDate = revisionStartDate;
	}
	public Date getRevisionEndDate() {
		return revisionEndDate;
	}
	public void setRevisionEndDate(Date revisionEndDate) {
		this.revisionEndDate = revisionEndDate;
	}
	public String getSignatureSection() {
		return signatureSection;
	}
	public void setSignatureSection(String signatureSection) {
		this.signatureSection = signatureSection;
	}
	public String getSignatureLine() {
		return signatureLine;
	}
	public void setSignatureLine(String signatureLine) {
		this.signatureLine = signatureLine;
	}
	public QuoteOEVO getCurrentOEQuoteVO() {
		return currentOEQuoteVO;
	}
	public void setCurrentOEQuoteVO(QuoteOEVO currentOEQuoteVO) {
		this.currentOEQuoteVO = currentOEQuoteVO;
	}
	public QuoteOEVO getRevisionOEQuoteVO() {
		return revisionOEQuoteVO;
	}
	public void setRevisionOEQuoteVO(QuoteOEVO revisionOEQuoteVO) {
		this.revisionOEQuoteVO = revisionOEQuoteVO;
	}
	public ClientContactVO getClientContactVO() {
		return clientContactVO;
	}
	public void setClientContactVO(ClientContactVO clientContactVO) {
		this.clientContactVO = clientContactVO;
	}
	public VehicleInformationVO getVehicleInformationVO() {
		return vehicleInformationVO;
	}
	public void setVehicleInformationVO(VehicleInformationVO vehicleInformationVO) {
		this.vehicleInformationVO = vehicleInformationVO;
	}
	public List<QuotationStepStructureVO> getCurrentQuotationStepStructureVOList() {
		return currentQuotationStepStructureVOList;
	}
	public void setCurrentQuotationStepStructureVOList(
			List<QuotationStepStructureVO> currentQuotationStepStructureVOList) {
		this.currentQuotationStepStructureVOList = currentQuotationStepStructureVOList;
	}
	public List<QuotationStepStructureVO> getRevisionQuotationStepStructureVOList() {
		return revisionQuotationStepStructureVOList;
	}
	public void setRevisionQuotationStepStructureVOList(
			List<QuotationStepStructureVO> revisionQuotationStepStructureVOList) {
		this.revisionQuotationStepStructureVOList = revisionQuotationStepStructureVOList;
	}
	public List<ServicesLeaseRateByPeriodVO> getCurrentServicesLeaseRateByPeriodVOList() {
		return currentServicesLeaseRateByPeriodVOList;
	}
	public void setCurrentServicesLeaseRateByPeriodVOList(
			List<ServicesLeaseRateByPeriodVO> currentServicesLeaseRateByPeriodVOList) {
		this.currentServicesLeaseRateByPeriodVOList = currentServicesLeaseRateByPeriodVOList;
	}
	public List<ServicesLeaseRateByPeriodVO> getRevisionServicesLeaseRateByPeriodVOList() {
		return revisionServicesLeaseRateByPeriodVOList;
	}
	public void setRevisionServicesLeaseRateByPeriodVOList(
			List<ServicesLeaseRateByPeriodVO> revisionServicesLeaseRateByPeriodVOList) {
		this.revisionServicesLeaseRateByPeriodVOList = revisionServicesLeaseRateByPeriodVOList;
	}	

}