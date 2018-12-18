package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;


public  class ArCreationVO implements Serializable {
		
	private static final long serialVersionUID = -4239603317198504794L;

	
	private long cId;
	private String sourceCode;
	private String transType;
	private String costDbCode;
	private String userName;
	private ExternalAccount externalAccount;
	private Date docDate;
	private FleetMaster fleetMaster;
	private Driver driver;
	private BigDecimal amount;
	private String categoryType;
	private String analysisCategory;
	private String analysisCode;
	private String lineDescription;
	private String chargeCode;
	private String glCode;

	public ArCreationVO(){}

	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getCostDbCode() {
		return costDbCode;
	}

	public void setCostDbCode(String costDbCode) {
		this.costDbCode = costDbCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getAnalysisCategory() {
		return analysisCategory;
	}

	public void setAnalysisCategory(String analysisCategory) {
		this.analysisCategory = analysisCategory;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public String getLineDescription() {
		return lineDescription;
	}

	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

}