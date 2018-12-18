package com.mikealbert.vision.vo;

import java.util.Date;
import java.util.List;

public class CustomerOrderConfirmationVO {
	private String customer;
	private String mailToName;
	private String mailToLine1;
	private String mailToLine2;
	private String mailToLine3;
	private String driverName;
	private String driverAddressLine1;
	private String driverAddressLine2;
	private String driverAddressLine3;
	private String driverAddressLine4;
	private String deliveryAddressLine1;
	private String deliveryAddressLine2;
	private String deliveryAddressLine3;
	private String deliveryAddressLine4;
	private String unitNo;
	private String trimDescription;
	private Date orderDate;
	private Date requestDate;
	private Date leaseExpirationDate;	
	private Date etaDate;
	private String replacesUnit;
	private String costCenter;
	private String fleetRefNo;
	private String rechargCode;
	private String exteriorColor;
	private String interiorColor;
	private List<EquipmentVO> standardEquipment;
	private List<EquipmentVO> factoryEquipment;
	private List<EquipmentVO> dealerEquipment;
	
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getMailToName() {
		return mailToName;
	}
	public void setMailToName(String mailToName) {
		this.mailToName = mailToName;
	}
	public String getMailToLine1() {
		return mailToLine1;
	}
	public void setMailToLine1(String mailToLine1) {
		this.mailToLine1 = mailToLine1;
	}
	public String getMailToLine2() {
		return mailToLine2;
	}
	public void setMailToLine2(String mailToLine2) {
		this.mailToLine2 = mailToLine2;
	}
	public String getMailToLine3() {
		return mailToLine3;
	}
	public void setMailToLine3(String mailToLine3) {
		this.mailToLine3 = mailToLine3;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverAddressLine1() {
		return driverAddressLine1;
	}
	public void setDriverAddressLine1(String driverAddressLine1) {
		this.driverAddressLine1 = driverAddressLine1;
	}
	public String getDriverAddressLine2() {
		return driverAddressLine2;
	}
	public void setDriverAddressLine2(String driverAddressLine2) {
		this.driverAddressLine2 = driverAddressLine2;
	}
	public String getDriverAddressLine3() {
		return driverAddressLine3;
	}
	public void setDriverAddressLine3(String driverAddressLine3) {
		this.driverAddressLine3 = driverAddressLine3;
	}
	public String getDriverAddressLine4() {
		return driverAddressLine4;
	}
	public void setDriverAddressLine4(String driverAddressLine4) {
		this.driverAddressLine4 = driverAddressLine4;
	}
	public String getDeliveryAddressLine1() {
		return deliveryAddressLine1;
	}
	public void setDeliveryAddressLine1(String deliveryAddressLine1) {
		this.deliveryAddressLine1 = deliveryAddressLine1;
	}
	public String getDeliveryAddressLine2() {
		return deliveryAddressLine2;
	}
	public void setDeliveryAddressLine2(String deliveryAddressLine2) {
		this.deliveryAddressLine2 = deliveryAddressLine2;
	}
	public String getDeliveryAddressLine3() {
		return deliveryAddressLine3;
	}
	public void setDeliveryAddressLine3(String deliveryAddressLine3) {
		this.deliveryAddressLine3 = deliveryAddressLine3;
	}
	public String getDeliveryAddressLine4() {
		return deliveryAddressLine4;
	}
	public void setDeliveryAddressLine4(String deliveryAddressLine4) {
		this.deliveryAddressLine4 = deliveryAddressLine4;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getTrimDescription() {
		return trimDescription;
	}
	public void setTrimDescription(String trimDescription) {
		this.trimDescription = trimDescription;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public Date getLeaseExpirationDate() {
		return leaseExpirationDate;
	}
	public void setLeaseExpirationDate(Date leaseExpirationDate) {
		this.leaseExpirationDate = leaseExpirationDate;
	}
	public Date getEtaDate() {
		return etaDate;
	}
	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}
	public String getReplacesUnit() {
		return replacesUnit;
	}
	public void setReplacesUnit(String replacesUnit) {
		this.replacesUnit = replacesUnit;
	}
	public String getCostCenter() {
		return costCenter;
	}
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	public String getFleetRefNo() {
		return fleetRefNo;
	}
	public void setFleetRefNo(String fleetRefNo) {
		this.fleetRefNo = fleetRefNo;
	}
	public String getRechargCode() {
		return rechargCode;
	}
	public void setRechargCode(String rechargCode) {
		this.rechargCode = rechargCode;
	}
	public String getExteriorColor() {
		return exteriorColor;
	}
	public void setExteriorColor(String exteriorColor) {
		this.exteriorColor = exteriorColor;
	}
	public String getInteriorColor() {
		return interiorColor;
	}
	public void setInteriorColor(String interiorColor) {
		this.interiorColor = interiorColor;
	}
	public List<EquipmentVO> getStandardEquipment() {
		return standardEquipment;
	}
	public void setStandardEquipment(List<EquipmentVO> standardEquipment) {
		this.standardEquipment = standardEquipment;
	}
	public List<EquipmentVO> getFactoryEquipment() {
		return factoryEquipment;
	}
	public void setFactoryEquipment(List<EquipmentVO> factoryEquipment) {
		this.factoryEquipment = factoryEquipment;
	}
	public List<EquipmentVO> getDealerEquipment() {
		return dealerEquipment;
	}
	public void setDealerEquipment(List<EquipmentVO> dealerEquipment) {
		this.dealerEquipment = dealerEquipment;
	}
	
}
