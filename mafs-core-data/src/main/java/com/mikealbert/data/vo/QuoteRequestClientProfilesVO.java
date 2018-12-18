package com.mikealbert.data.vo;

import java.util.Date;

public class QuoteRequestClientProfilesVO {

	Long qprId;
	String interestType;
	String costTreatment;
	String productType;
	String financeParams;
	String mileageProgram;
	String mileageBand;
	String clientParams;
	String profileCodeDescription;
	Date effectiveFrom;
	
	
	public Long getQprId() {
		return qprId;
	}
	public void setQprId(Long qprId) {
		this.qprId = qprId;
	}
	public String getInterestType() {
		return interestType;
	}
	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}
	public String getCostTreatment() {
		return costTreatment;
	}
	public void setCostTreatment(String costTreatment) {
		this.costTreatment = costTreatment;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getFinanceParams() {
		return financeParams;
	}
	public void setFinanceParams(String financeParams) {
		this.financeParams = financeParams;
	}
	public String getMileageProgram() {
		return mileageProgram;
	}
	public void setMileageProgram(String mileageProgram) {
		this.mileageProgram = mileageProgram;
	}
	public String getMileageBand() {
		return mileageBand;
	}
	public void setMileageBand(String mileageBand) {
		this.mileageBand = mileageBand;
	}
	public String getClientParams() {
		return clientParams;
	}
	public void setClientParams(String clientParams) {
		this.clientParams = clientParams;
	}
	public String getProfileCodeDescription() {
		return profileCodeDescription;
	}
	public void setProfileCodeDescription(String profileCodeDescription) {
		this.profileCodeDescription = profileCodeDescription;
	}
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
}
