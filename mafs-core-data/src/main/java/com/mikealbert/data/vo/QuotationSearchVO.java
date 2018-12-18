package com.mikealbert.data.vo;

import java.util.Date;

import com.mikealbert.data.vo.ModelSearchCriteriaVO;

public class QuotationSearchVO extends ModelSearchCriteriaVO {
	private String unitNo;
	private Long cId;
	private String accountType;
	private String accountCode;
	private String accountShortName;
	private String accountName;
	private String modelDescription;
	private String vinNumber;
	private String quoteRefNo;
	private Long fmsId;
	private Long driverId;
	private String driverName;
	private String gradeGroupCode;
	private String gradeGroupDescription;
	private Long quoId;
	private Long qmdId;
	private Long contractPeriod;
	private Long projectedMonths;
	private Date contractStartDate;
	private String fleetRefNo;
	private String uniqueRowId;
		

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getVinNumber() {
		return vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public String getGradeGroupCode() {
		return gradeGroupCode;
	}

	public void setGradeGroupCode(String gradeGroupCode) {
		this.gradeGroupCode = gradeGroupCode;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Long getQuoId() {
		return quoId;
	}

	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public Long getContractPeriod() {
		return contractPeriod;
	}

	public void setContractPeriod(Long contractPeriod) {
		this.contractPeriod = contractPeriod;
	}

	public Long getProjectedMonths() {
		return projectedMonths;
	}

	public void setProjectedMonths(Long projectedMonths) {
		this.projectedMonths = projectedMonths;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public String getFleetRefNo() {
		return fleetRefNo;
	}

	public void setFleetRefNo(String fleetRefNo) {
		this.fleetRefNo = fleetRefNo;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountShortName() {
		return accountShortName;
	}

	public void setAccountShortName(String accountShortName) {
		this.accountShortName = accountShortName;
	}

	public String getQuoteRefNo() {
		return quoteRefNo;
	}

	public void setQuoteRefNo(String quoteRefNo) {
		this.quoteRefNo = quoteRefNo;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public String getGradeGroupDescription() {
		return gradeGroupDescription;
	}

	public void setGradeGroupDescription(String gradeGroupDescription) {
		this.gradeGroupDescription = gradeGroupDescription;
	}

	public String getUniqueRowId() {
		return uniqueRowId;
	}

	public void setUniqueRowId(String uniqueRowId) {
		this.uniqueRowId = uniqueRowId;
	}
	

}
