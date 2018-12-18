package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class ClientCreditLimitsVO {
	private String creditStatus;
	private BigDecimal balanceCeilLimit;
	private BigDecimal balanceCurrentLimit;
	private BigDecimal capitalCeilLimit;
	private BigDecimal capitalCurrentLimit;
	private BigDecimal unitCeilLimit;
	private BigDecimal unitCurrentLimit;
	private String error;
	
	public String getCreditStatus() {
		return creditStatus;
	}
	public void setCreditStatus(String creditStatus) {
		this.creditStatus = creditStatus;
	}
	public BigDecimal getBalanceCeilLimit() {
		return balanceCeilLimit;
	}
	public void setBalanceCeilLimit(BigDecimal balanceCeilLimit) {
		this.balanceCeilLimit = balanceCeilLimit;
	}
	public BigDecimal getBalanceCurrentLimit() {
		return balanceCurrentLimit;
	}
	public void setBalanceCurrentLimit(BigDecimal balanceCurrentLimit) {
		this.balanceCurrentLimit = balanceCurrentLimit;
	}
	public BigDecimal getCapitalCeilLimit() {
		return capitalCeilLimit;
	}
	public void setCapitalCeilLimit(BigDecimal capitalCeilLimit) {
		this.capitalCeilLimit = capitalCeilLimit;
	}
	public BigDecimal getCapitalCurrentLimit() {
		return capitalCurrentLimit;
	}
	public void setCapitalCurrentLimit(BigDecimal capitalCurrentLimit) {
		this.capitalCurrentLimit = capitalCurrentLimit;
	}
	public BigDecimal getUnitCeilLimit() {
		return unitCeilLimit;
	}
	public void setUnitCeilLimit(BigDecimal unitCeilLimit) {
		this.unitCeilLimit = unitCeilLimit;
	}
	public BigDecimal getUnitCurrentLimit() {
		return unitCurrentLimit;
	}
	public void setUnitCurrentLimit(BigDecimal unitCurrentLimit) {
		this.unitCurrentLimit = unitCurrentLimit;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	}
