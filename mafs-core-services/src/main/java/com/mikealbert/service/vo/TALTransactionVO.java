package com.mikealbert.service.vo;

public class TALTransactionVO {
	
	Long fmsId;
	String unitNumber;
	String transactionCode;
	String countryCode;
	String regionCode;
	Long driverId;
	Long contextId;
	String accountCode;
	String accountType;
	String plateTypeCode;
	String reasonCode;
	String inventoryCode;	
	boolean fetchDriverAddress;
	Long userId;
	boolean checkIfExist;
	String txnOriginPGM;
	
	
	public String getUnitNumber() {
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getPlateTypeCode() {
		return plateTypeCode;
	}
	public void setPlateTypeCode(String plateTypeCode) {
		this.plateTypeCode = plateTypeCode;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getInventoryCode() {
		return inventoryCode;
	}
	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}
	public boolean isFetchDriverAddress() {
		return fetchDriverAddress;
	}
	public void setFetchDriverAddress(boolean fetchDriverAddress) {
		this.fetchDriverAddress = fetchDriverAddress;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getFmsId() {
		return fmsId;
	}
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}
	public boolean isCheckIfExist() {
		return checkIfExist;
	}
	public void setCheckIfExist(boolean checkIfExist) {
		this.checkIfExist = checkIfExist;
	}
	public String getTxnOriginPGM() {
		return txnOriginPGM;
	}
	public void setTxnOriginPGM(String txnOriginPGM) {
		this.txnOriginPGM = txnOriginPGM;
	}
	

	@Override
	public String toString() {
		return "TALTransactionVO [fmsId=" + fmsId + ", unitNumber="
				+ unitNumber + ", transactionCode=" + transactionCode
				+ ", countryCode=" + countryCode + ", regionCode=" + regionCode
				+ ", reasonCode=" + reasonCode + ", inventoryCode="
				+ inventoryCode + ", userId=" + userId + "]";
	}
	
}
