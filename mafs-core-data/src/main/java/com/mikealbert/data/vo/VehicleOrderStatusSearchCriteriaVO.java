package com.mikealbert.data.vo;


public class VehicleOrderStatusSearchCriteriaVO {
	private String unitNo;
	private String purchaseOrderNumber;
	private String factoryNo;
	
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo.trim();
	}
	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}
	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber.trim();
	}
	public String getFactoryNo() {
		return factoryNo;
	}
	public void setFactoryNo(String factoryNo) {
		this.factoryNo = factoryNo;
	}

	
}
