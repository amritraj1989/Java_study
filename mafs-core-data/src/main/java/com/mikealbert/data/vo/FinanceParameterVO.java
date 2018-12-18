package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;


import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.WorkClassAuthority;

public class FinanceParameterVO implements Serializable {
		
	private static final long serialVersionUID = 1L;

	private FormulaParameter formulaParameter;
	private FinanceParameter financeParameter;
	private WorkClassAuthority workClassAuthority;
	private QuotationModelFinances quotationModelFinances;
	private BigDecimal value;
	private boolean changed;
	private BigDecimal defaultValue;
	private BigDecimal totalUnpaidAmount;
	private BigDecimal monthlyValue;
	private BigDecimal totalValueExcludingInterest;
	
		
		
	public FinanceParameterVO(){}


	public FormulaParameter getFormulaParameter() {
		return formulaParameter;
	}

	public void setFormulaParameter(FormulaParameter formulaParameter) {
		this.formulaParameter = formulaParameter;
	}

	public QuotationModelFinances getQuotationModelFinances() {
		return quotationModelFinances;
	}

	public void setQuotationModelFinances(QuotationModelFinances quotationModelFinances) {
		this.quotationModelFinances = quotationModelFinances;
	}


	public FinanceParameter getFinanceParameter() {
		return financeParameter;
	}


	public void setFinanceParameter(FinanceParameter financeParameter) {
		this.financeParameter = financeParameter;
	}


	public WorkClassAuthority getWorkClassAuthority() {
		return workClassAuthority;
	}


	public void setWorkClassAuthority(WorkClassAuthority workClassAuthority) {
		this.workClassAuthority = workClassAuthority;
	}


	public BigDecimal getValue() {
		return value;
	}


	public void setValue(BigDecimal value) {
		this.value = value;
	}


	public boolean isChanged() {
		return changed;
	}


	public void setChanged(boolean changed) {
		this.changed = changed;
	}


	public BigDecimal getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(BigDecimal defaultValue) {
		this.defaultValue = defaultValue;
	}


	public BigDecimal getTotalUnpaidAmount() {
		return totalUnpaidAmount;
	}


	public void setTotalUnpaidAmount(BigDecimal totalUnpaidAmount) {
		this.totalUnpaidAmount = totalUnpaidAmount;
	}


	public BigDecimal getMonthlyValue() {
		return monthlyValue;
	}


	public void setMonthlyValue(BigDecimal monthlyValue) {
		this.monthlyValue = monthlyValue;
	}


	public BigDecimal getTotalValueExcludingInterest() {
		return totalValueExcludingInterest;
	}


	public void setTotalValueExcludingInterest(
			BigDecimal totalValueExcludingInterest) {
		this.totalValueExcludingInterest = totalValueExcludingInterest;
	}

}