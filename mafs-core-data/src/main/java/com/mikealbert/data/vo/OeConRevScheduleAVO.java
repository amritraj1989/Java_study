package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.QuotationModel;

public class OeConRevScheduleAVO {
	private String logo;
	private String oeContractRevisionScheduleATitle;
	private String address;
	private String driverAddress;
	private String costCenter;
	private String interestComponent;
	private String summaryText;
	private QuoteOEVO quoteOEVO;
	private VehicleInformationVO vehicleInformationVO;
	private VehicleInfoVO vehicleInfoVO;
	private DeliveringDealerInfoVO deliveringDealerInfoVO;
	private List<QuotationStepStructureVO> quotationStepStructureVOList;
	private List<QuotationStepStructureVO> currentQuotationStepStructureVOList;
	private List<QuotationStepStructureVO> revisionQuotationStepStructureVOList;	
	private List<ServicesLeaseRateByPeriodVO> servicesLeaseRateVOList;
	private String referenceQuote;
	private String rechargeCode;  //comes from the driver
	private String excessMileage; //comes from quotation profile / excess mileage
	private BigDecimal securityDeposit;
	private String masterLeaseAgreementNo;
	private Date inServiceDate;
	
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getOeContractRevisionScheduleATitle() {
		return oeContractRevisionScheduleATitle;
	}
	public void setOeContractRevisionScheduleATitle(
			String oeContractRevisionScheduleATitle) {
		this.oeContractRevisionScheduleATitle = oeContractRevisionScheduleATitle;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDriverAddress() {
		return driverAddress;
	}
	public void setDriverAddress(String driverAddress) {
		this.driverAddress = driverAddress;
	}
	public String getCostCenter() {
		return costCenter;
	}
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	public String getInterestComponent() {
		return interestComponent;
	}
	public void setInterestComponent(String interestComponent) {
		this.interestComponent = interestComponent;
	}
	public String getSummaryText() {
		return summaryText;
	}
	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}
	public QuoteOEVO getQuoteOEVO() {
		return quoteOEVO;
	}
	public void setQuoteOEVO(QuoteOEVO quoteOEVO) {
		this.quoteOEVO = quoteOEVO;
	}
	public VehicleInformationVO getVehicleInformationVO() {
		return vehicleInformationVO;
	}
	public void setVehicleInformationVO(VehicleInformationVO vehicleInformationVO) {
		this.vehicleInformationVO = vehicleInformationVO;
	}
	public VehicleInfoVO getVehicleInfoVO() {
		return vehicleInfoVO;
	}
	public void setVehicleInfoVO(VehicleInfoVO vehicleInfoVO) {
		this.vehicleInfoVO = vehicleInfoVO;
	}
	public DeliveringDealerInfoVO getDeliveringDealerInfoVO() {
		return deliveringDealerInfoVO;
	}
	public void setDeliveringDealerInfoVO(DeliveringDealerInfoVO deliveringDealerInfoVO) {
		this.deliveringDealerInfoVO = deliveringDealerInfoVO;
	}
	public List<QuotationStepStructureVO> getQuotationStepStructureVOList() {
		return quotationStepStructureVOList;
	}
	public void setQuotationStepStructureVOList(
			List<QuotationStepStructureVO> quotationStepStructureVOList) {
		this.quotationStepStructureVOList = quotationStepStructureVOList;
	}
	public List<QuotationStepStructureVO> getCurrentQuotationStepStructureVOList() {
		return currentQuotationStepStructureVOList;
	}
	public void setCurrentQuotationStepStructureVOList(List<QuotationStepStructureVO> currentQuotationStepStructureVOList) {
		this.currentQuotationStepStructureVOList = currentQuotationStepStructureVOList;
	}
	public List<QuotationStepStructureVO> getRevisionQuotationStepStructureVOList() {
		return revisionQuotationStepStructureVOList;
	}
	public void setRevisionQuotationStepStructureVOList(List<QuotationStepStructureVO> revisionQuotationStepStructureVOList) {
		this.revisionQuotationStepStructureVOList = revisionQuotationStepStructureVOList;
	}
	public List<ServicesLeaseRateByPeriodVO> getServicesLeaseRateVOList() {
		return servicesLeaseRateVOList;
	}
	public void setServicesLeaseRateVOList(List<ServicesLeaseRateByPeriodVO> servicesLeaseRateVOList) {
		this.servicesLeaseRateVOList = servicesLeaseRateVOList;
	}
	public String getReferenceQuote() {
		return referenceQuote;
	}
	public void setReferenceQuote(String referenceQuote) {
		this.referenceQuote = referenceQuote;
	}
	public String getRechargeCode() {
		return rechargeCode;
	}
	public void setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
	}
	public String getExcessMileage() {
		return excessMileage;
	}
	public void setExcessMileage(String excessMileage) {
		this.excessMileage = excessMileage;
	}
	public BigDecimal getSecurityDeposit() {
		return securityDeposit;
	}
	public void setSecurityDeposit(BigDecimal securityDeposit) {
		this.securityDeposit = securityDeposit;
	}
	public String getMasterLeaseAgreementNo() {
		return masterLeaseAgreementNo;
	}
	public void setMasterLeaseAgreementNo(String masterLeaseAgreementNo) {
		this.masterLeaseAgreementNo = masterLeaseAgreementNo;
	}
	public Date getInServiceDate() {
		return inServiceDate;
	}
	public void setInServiceDate(Date inServiceDate) {
		this.inServiceDate = inServiceDate;
	}
	
	
}