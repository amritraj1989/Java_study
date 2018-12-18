package com.mikealbert.data.vo;

import com.mikealbert.data.vo.ModelSearchCriteriaVO;

public class OrderProgressSearchCriteriaVO extends ModelSearchCriteriaVO {
	private String orderType;
	private String unitNo;
	private String factoryEquipment;
	private String client;
		
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getFactoryEquipment() {
		return factoryEquipment;
	}

	public void setFactoryEquipment(String factoryEquipment) {
		this.factoryEquipment = factoryEquipment;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
	

}
