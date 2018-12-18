package com.mikealbert.data.vo;

import java.io.Serializable;

public class DriverLOVVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4654660321211957634L;
	
	Long drvId;
	String driverSurname;
	String driverForename;
	String poolManager;
	String accountName;
	String accountId;
	String accountStatus;
	String businessAddressLine;
	String addressLine1;
	String addressLine2;
	String townCity;
	String region;
	String postcode;
	String email;
	String activeInd;
	
	
	public Long getDrvId() {
		return drvId;
	}
	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}
	public String getDriverSurname() {
		return driverSurname;
	}
	public void setDriverSurname(String driverSurname) {
		this.driverSurname = driverSurname;
	}
	public String getDriverForename() {
		return driverForename;
	}
	public void setDriverForename(String driverForename) {
		this.driverForename = driverForename;
	}
	public String getPoolManager() {
		return poolManager;
	}
	public void setPoolManager(String poolManager) {
		this.poolManager = poolManager;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getBusinessAddressLine() {
		return businessAddressLine;
	}
	public void setBusinessAddressLine(String businessAddressLine) {
		this.businessAddressLine = businessAddressLine;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}	
	
	public String getTownCity() {
		return townCity;
	}
	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getActiveInd() {
		return activeInd;
	}
	public void setActiveInd(String activeInd) {
		this.activeInd = activeInd;
	}
	
}
