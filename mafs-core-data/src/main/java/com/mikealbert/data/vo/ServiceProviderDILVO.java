package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class ServiceProviderDILVO {
	private BigDecimal id;
	private String lat;
	private String lng;
	private BigDecimal distance;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postCode;
	private String telephone;
	private String storeNumber;
	private String billThroughName;
	private BigDecimal yearlyEventCount;
	private String lastEventDate;
	private boolean potentialDIL = false;
	
	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	public String getBillThroughName() {
		return billThroughName;
	}

	public void setBillThroughName(String billThroughName) {
		this.billThroughName = billThroughName;
	}

	public BigDecimal getYearlyEventCount() {
		return yearlyEventCount;
	}

	public void setYearlyEventCount(BigDecimal yearlyEventCount) {
		this.yearlyEventCount = yearlyEventCount;
	}

	public String getLastEventDate() {
		return lastEventDate;
	}

	public void setLastEventDate(String lastEventDate) {
		this.lastEventDate = lastEventDate;
	}

	public boolean isPotentialDIL() {
		return potentialDIL;
	}

	public void setPotentialDIL(boolean potentialDIL) {
		this.potentialDIL = potentialDIL;
	}
}
