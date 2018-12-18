package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ClientFinanceVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long clientFinanceId;
	private Long clientFinanceTypeId;
	private Long eaCId;
	private String eaAccountType;
	private String eaAccountCode;
	private Long clientGradeGroupId;
	private String clientGradeGroupCode;
	private String clientGradeGroupDesc;
	private int clientCostCentreLevel;
	private String clientCostCentreCode;
	private String clientCostCentreDesc;
	private String clientCostCentreParent;
	private String parameterId;	
	private String status;
	private Date lastUpdated;
	private String financeParameterValue;
	private String description;
	private Long financeParamCatId;
	private String financeParamCategory;
	private BigDecimal defaultnvalue;
	private String defaultcvalue;
	private String ggIndicator;
	private String ccIndicator;
	private String finParamAllow;
	
	public ClientFinanceVO(){}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFinanceParamCategory() {
		return financeParamCategory;
	}

	public void setFinanceParamCategory(String financeParamCategory) {
		this.financeParamCategory = financeParamCategory;
	}

	public Long getEaCId() {
		return eaCId;
	}

	public void setEaCId(Long eaCId) {
		this.eaCId = eaCId;
	}

	public String getEaAccountType() {
		return eaAccountType;
	}

	public void setEaAccountType(String eaAccountType) {
		this.eaAccountType = eaAccountType;
	}

	public String getEaAccountCode() {
		return eaAccountCode;
	}

	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}

	public Long getClientGradeGroupId() {
		return clientGradeGroupId;
	}

	public void setClientGradeGroupId(Long clientGradeGroupId) {
		this.clientGradeGroupId = clientGradeGroupId;
	}

	public String getClientGradeGroupCode() {
		return clientGradeGroupCode;
	}

	public void setClientGradeGroupCode(String clientGradeGroupCode) {
		this.clientGradeGroupCode = clientGradeGroupCode;
	}

	public String getClientGradeGroupDesc() {
		return clientGradeGroupDesc;
	}

	public void setClientGradeGroupDesc(String clientGradeGroupDesc) {
		this.clientGradeGroupDesc = clientGradeGroupDesc;
	}

	public int getClientCostCentreLevel() {
		return clientCostCentreLevel;
	}

	public void setClientCostCentreLevel(int clientCostCentreLevel) {
		this.clientCostCentreLevel = clientCostCentreLevel;
	}

	public String getClientCostCentreCode() {
		return clientCostCentreCode;
	}

	public void setClientCostCentreCode(String clientCostCentreCode) {
		this.clientCostCentreCode = clientCostCentreCode;
	}

	public String getClientCostCentreDesc() {
		return clientCostCentreDesc;
	}

	public void setClientCostCentreDesc(String clientCostCentreDesc) {
		this.clientCostCentreDesc = clientCostCentreDesc;
	}

	public String getClientCostCentreParent() {
		return clientCostCentreParent;
	}

	public void setClientCostCentreParent(String clientCostCentreParent) {
		this.clientCostCentreParent = clientCostCentreParent;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getFinanceParamCatId() {
		return financeParamCatId;
	}

	public void setFinanceParamCatId(Long financeParamCatId) {
		this.financeParamCatId = financeParamCatId;
	}

	public String getFinanceParameterValue() {
		return financeParameterValue;
	}

	public void setFinanceParameterValue(String financeParameterValue) {
		this.financeParameterValue = financeParameterValue;
	}

	public BigDecimal getDefaultnvalue() {
		return defaultnvalue;
	}

	public void setDefaultnvalue(BigDecimal defaultnvalue) {
		this.defaultnvalue = defaultnvalue;
	}

	public String getDefaultcvalue() {
		return defaultcvalue;
	}

	public void setDefaultcvalue(String defaultcvalue) {
		this.defaultcvalue = defaultcvalue;
	}

	public Long getClientFinanceId() {
		return clientFinanceId;
	}

	public void setClientFinanceId(Long clientFinanceId) {
		this.clientFinanceId = clientFinanceId;
	}

	public Long getClientFinanceTypeId() {
		return clientFinanceTypeId;
	}

	public void setClientFinanceTypeId(Long clientFinanceTypeId) {
		this.clientFinanceTypeId = clientFinanceTypeId;
	}

	public String getGgIndicator() {
		return ggIndicator;
	}

	public void setGgIndicator(String ggIndicator) {
		this.ggIndicator = ggIndicator;
	}

	public String getCcIndicator() {
		return ccIndicator;
	}

	public void setCcIndicator(String ccIndicator) {
		this.ccIndicator = ccIndicator;
	}

	public String getFinParamAllow() {
		return finParamAllow;
	}

	public void setFinParamAllow(String finParamAllow) {
		this.finParamAllow = finParamAllow;
	}
}
