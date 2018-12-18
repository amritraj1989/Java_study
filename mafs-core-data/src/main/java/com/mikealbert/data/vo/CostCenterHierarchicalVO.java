package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.List;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.util.MALUtilities;

public class CostCenterHierarchicalVO implements Serializable {
	private static final long serialVersionUID = -2400721099734693703L;
	
	private Long level;	
	private Long corporateEntityId;
	private String accountType;
	private String accountCode;
	private String code;
	private String description;
	private String parentCode;
	private Long parentCorporateEntityId;
	private String parentAccountType;
	private String parentAccountCode;
	
	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getCorporateEntityId() {
		return corporateEntityId;
	}

	public void setCorporateEntityId(Long corporateEntityId) {
		this.corporateEntityId = corporateEntityId;
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

	public CostCenterHierarchicalVO(){}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Long getParentCorporateEntityId() {
		return parentCorporateEntityId;
	}

	public void setParentCorporateEntityId(Long parentCorporateEntityId) {
		this.parentCorporateEntityId = parentCorporateEntityId;
	}

	public String getParentAccountType() {
		return parentAccountType;
	}

	public void setParentAccountType(String parentAccountType) {
		this.parentAccountType = parentAccountType;
	}

	public String getParentAccountCode() {
		return parentAccountCode;
	}

	public void setParentAccountCode(String parentAccountCode) {
		this.parentAccountCode = parentAccountCode;
	}

}
