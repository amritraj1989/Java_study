package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the INTEREST_DISCLOSURES database table.
 * 
 */
@Entity
@Table(name="INTEREST_DISCLOSURES")
public class InterestDisclosure implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="IDIS_ID")
	private String idisId;	
	
	@Column(name="LEASE_TYPE")
	private String leaseType;
	
	@Column(name="DISCLOSURE_IND")
	private String disclosureInd;	
	
	@Column(name="INTEREST_RATE_TYPE")
	private String interestRateType;
	
	@Column(name="PRE_CON_INT_FLAG")
	private String preConIntFlag;	
	
	@Column(name="PRE_CON_COST_FLAG")
	private String preConCostFlag;	
	
	@Column(name="CALCULATION_TEXT")
	private String calculationText;	
	
	@Column(name="LEASE_RATE_TEXT")
	private String leaseRateText;	
	
	@Column(name="DATE_FROM")
	private String dateFrom;
	
	@Column(name="DATE_TO")
	private String dateTo;	
	
	@Column(name="LAST_ACTION_USER")
	private String lastActionUser;	
	
	@Column(name="LAST_ACTION_DATE")
	private String lastActionDate;	
	
	@Column(name="VARIABLE_RATE")
	private String variableRate;		
	
	@Column(name="FORMAL_EXT")
	private String formalExt;

	public String getIdisId() {
		return idisId;
	}

	public String getLeaseType() {
		return leaseType;
	}

	public String getDisclosureInd() {
		return disclosureInd;
	}

	public String getInterestRateType() {
		return interestRateType;
	}

	public String getPreConIntFlag() {
		return preConIntFlag;
	}

	public String getPreConCostFlag() {
		return preConCostFlag;
	}

	public String getCalculationText() {
		return calculationText;
	}

	public String getLeaseRateText() {
		return leaseRateText;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public String getLastActionUser() {
		return lastActionUser;
	}

	public String getLastActionDate() {
		return lastActionDate;
	}

	public String getVariableRate() {
		return variableRate;
	}

	public String getFormalExt() {
		return formalExt;
	}

	public void setIdisId(String idisId) {
		this.idisId = idisId;
	}

	public void setLeaseType(String leaseType) {
		this.leaseType = leaseType;
	}

	public void setDisclosureInd(String disclosureInd) {
		this.disclosureInd = disclosureInd;
	}

	public void setInterestRateType(String interestRateType) {
		this.interestRateType = interestRateType;
	}

	public void setPreConIntFlag(String preConIntFlag) {
		this.preConIntFlag = preConIntFlag;
	}

	public void setPreConCostFlag(String preConCostFlag) {
		this.preConCostFlag = preConCostFlag;
	}

	public void setCalculationText(String calculationText) {
		this.calculationText = calculationText;
	}

	public void setLeaseRateText(String leaseRateText) {
		this.leaseRateText = leaseRateText;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setLastActionUser(String lastActionUser) {
		this.lastActionUser = lastActionUser;
	}

	public void setLastActionDate(String lastActionDate) {
		this.lastActionDate = lastActionDate;
	}

	public void setVariableRate(String variableRate) {
		this.variableRate = variableRate;
	}

	public void setFormalExt(String formalExt) {
		this.formalExt = formalExt;
	}		

}
