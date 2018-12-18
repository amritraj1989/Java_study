package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;


public  class ServiceProviderDiscountVO implements Serializable {
		
	private static final long serialVersionUID = -4239603387198504794L;

	
	private long mcoId;
	private String maintCode;
	private String maintCodeDescription;
	private String maintCategoryCode;
	private boolean discount;

	public ServiceProviderDiscountVO(){}

	public long getMcoId() {
		return mcoId;
	}

	public void setMcoId(long mcoId) {
		this.mcoId = mcoId;
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

	public boolean isDiscount() {
		return discount;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}


}