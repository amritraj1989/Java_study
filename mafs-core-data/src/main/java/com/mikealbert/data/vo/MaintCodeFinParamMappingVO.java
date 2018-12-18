package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

public class MaintCodeFinParamMappingVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long maintCodeFinParamMapId;
	private Long maintCodeId;
	private String maintCode;
	private String maintCodeDescription;
	private String maintCategoryCode;
	private String financeParameterId;
	private String financeParameterKey;
	private Date financeParameterEffectiveFrom;
	private String financeParameterDescription;	
	
	public MaintCodeFinParamMappingVO(){};	
	
	public Long getMaintCodeFinParamMapId() {
		return maintCodeFinParamMapId;
	}

	public void setMaintCodeFinParamMapId(Long maintCodeFinParamMapId) {
		this.maintCodeFinParamMapId = maintCodeFinParamMapId;
	}
		
	public Long getMaintCodeId() {
		return maintCodeId;
	}

	public void setMaintCodeId(Long maintCodeId) {
		this.maintCodeId = maintCodeId;
	}

	public String getMaintCode() {
		return maintCode;
	}
	public void setMaintCode(String maintCode) {
		this.maintCode = maintCode;
	}

	public String getMaintCodeDescription() {
		return maintCodeDescription;
	}

	public void setMaintCodeDescription(String maintCodeDescription) {
		this.maintCodeDescription = maintCodeDescription;
	}

	public String getMaintCategoryCode() {
		return maintCategoryCode;
	}

	public void setMaintCategoryCode(String maintCategoryCode) {
		this.maintCategoryCode = maintCategoryCode;
	}

	public String getFinanceParameterId() {
		return financeParameterId;
	}

	public void setFinanceParameterId(String financeParameterId) {
		this.financeParameterId = financeParameterId;
	}

	public String getFinanceParameterKey() {
		return financeParameterKey;
	}

	public void setFinanceParameterKey(String financeParameterKey) {
		this.financeParameterKey = financeParameterKey;
	}


	public Date getFinanceParameterEffectiveFrom() {
		return financeParameterEffectiveFrom;
	}

	public void setFinanceParameterEffectiveFrom(Date financeParameterEffectiveFrom) {
		this.financeParameterEffectiveFrom = financeParameterEffectiveFrom;
	}

	public String getFinanceParameterDescription() {
		return financeParameterDescription;
	}

	public void setFinanceParameterDescription(
			String financeParameterDescription) {
		this.financeParameterDescription = financeParameterDescription;
	}

}
