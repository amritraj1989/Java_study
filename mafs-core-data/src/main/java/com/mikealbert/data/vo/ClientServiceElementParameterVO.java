package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ClientServiceElementParameterVO {

	private Long clientServiceElementParameterId;
	private Long clientServiceElementId;
	private Long formulaParameterId;
	private BigDecimal defaultValue;
	private BigDecimal clientValue;
	private BigDecimal gradeGroupValue;
	private BigDecimal productValue;
	private String parameterId;
	private String parameterKey;
	private String parameterDescription;
	private Date lastUpdated;
	public Long getClientServiceElementParameterId() {
		return clientServiceElementParameterId;
	}
	public void setClientServiceElementParameterId(
			Long clientServiceElementParameterId) {
		this.clientServiceElementParameterId = clientServiceElementParameterId;
	}
	public Long getClientServiceElementId() {
		return clientServiceElementId;
	}
	public void setClientServiceElementId(Long clientServiceElementId) {
		this.clientServiceElementId = clientServiceElementId;
	}
	public Long getFormulaParameterId() {
		return formulaParameterId;
	}
	public void setFormulaParameterId(Long formulaParameterId) {
		this.formulaParameterId = formulaParameterId;
	}
	public BigDecimal getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(BigDecimal defaultValue) {
		this.defaultValue = defaultValue;
	}
	public BigDecimal getClientValue() {
		return clientValue;
	}
	public void setClientValue(BigDecimal clientValue) {
		this.clientValue = clientValue;
	}
	public BigDecimal getGradeGroupValue() {
		return gradeGroupValue;
	}
	public void setGradeGroupValue(BigDecimal gradeGroupValue) {
		this.gradeGroupValue = gradeGroupValue;
	}
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	public String getParameterKey() {
		return parameterKey;
	}
	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}
	public String getParameterDescription() {
		return parameterDescription;
	}
	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public BigDecimal getProductValue() {
		return productValue;
	}
	public void setProductValue(BigDecimal productValue) {
		this.productValue = productValue;
	}
}
