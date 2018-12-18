package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class QuotationProfileFinanceVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String profileCode;
	private String profileDescription;
	private BigDecimal profileFinanceNumericValue;
	private String profileFinanceCharValue;
	
	public String getProfileCode() {
		return profileCode;
	}
	
	public void setProfileCode(String profileCode) {
		this.profileCode = profileCode;
	}
	
	public String getProfileDescription() {
		return profileDescription;
	}
	
	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}
	
	public BigDecimal getProfileFinanceNumericValue() {
		return profileFinanceNumericValue;
	}
	
	public void setProfileFinanceNumericValue(BigDecimal profileFinanceNumericValue) {
		this.profileFinanceNumericValue = profileFinanceNumericValue;
	}
	
	public String getProfileFinanceCharValue() {
		return profileFinanceCharValue;
	}
	
	public void setProfileFinanceCharValue(String profileFinanceCharValue) {
		this.profileFinanceCharValue = profileFinanceCharValue;
	}
	
}
