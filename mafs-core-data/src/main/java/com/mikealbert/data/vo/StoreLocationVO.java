package com.mikealbert.data.vo;

import java.io.Serializable;

import com.mikealbert.data.annotation.MafsUISequence;
import com.mikealbert.data.entity.ServiceProvider;

public class StoreLocationVO implements Serializable {
	private static final long serialVersionUID = -8715923994927048069L;
	
	public StoreLocationVO(ServiceProvider provider){
		this.parentProvider 	= provider.getParentServiceProvider().getServiceProviderName()  + " No: " + provider.getParentServiceProvider().getServiceProviderName();
		this.storeCode 			= provider.getServiceProviderNumber();
		this.storeName 			= provider.getServiceProviderName();
		this.telephoneNumber 	= provider.getTelephoneNo();
		this.faxNumber 			= provider.getFaxNo();
		this.emailAddress 		= provider.getEmailAddress();
		this.numberOfBays 		= provider.getNoOfBays();
		this.addressLine1 		= provider.getServiceProviderAddresses().get(0).getAddressLine1();
		this.addressLine2 		= provider.getServiceProviderAddresses().get(0).getAddressLine2();
		this.addressLine3 		= provider.getServiceProviderAddresses().get(0).getAddressLine3();
		this.addressLine4 		= provider.getServiceProviderAddresses().get(0).getAddressLine4();
		this.zipCode 			= provider.getServiceProviderAddresses().get(0).getPostcode();
		this.city 				= provider.getServiceProviderAddresses().get(0).getTownCity();
		this.stateProv 			= provider.getServiceProviderAddresses().get(0).getRegion();
		//TODO: is this needed?
		this.operationCode 		= "";
	}
	
	public StoreLocationVO(){}
	
	private String 	messageId;
	private String 	parentProvider;
	private String 	storeCode;                     
	private String 	storeName;                     
	private String 	telephoneNumber;
	private String 	faxNumber;           
	private String 	emailAddress;               
	private String 	weekdayStartTime;
	private String 	weekdayEndTime;
	private String 	saturdayStartTime;
	private String 	saturdayEndTime;
	private String 	sundayStartTime;
	private String 	sundayEndTime;
	private Long 	numberOfBays;
	private Long 	clearanceInFeet;
	private String 	addressLine1;
	private String 	addressLine2;
	private String 	addressLine3;
	private String 	addressLine4;
	private String 	zipCode;
	private String 	city;
	private String 	stateProv;
	private String 	countyCode;
	private String 	countryCode;
	private String 	operationCode;
    
	
	public String getMessageId() {
		return messageId;
	}
	
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getParentProvider() {
		return parentProvider;
	}
	
	public void setParentProvider(String parentProvider) {
		this.parentProvider = parentProvider;
	}	
	
	@MafsUISequence("1")
	public String getStoreCode() {
		return storeCode;
	}
	
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	
	@MafsUISequence("2")
	public String getStoreName() {
		return storeName;
	}
	
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	@MafsUISequence("3")
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	
	@MafsUISequence("4")
	public String getFaxNumber() {
		return faxNumber;
	}
	
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	
	@MafsUISequence("5")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@MafsUISequence("6")
	public String getWeekdayStartTime() {
		return weekdayStartTime;
	}
	
	public void setWeekdayStartTime(String weekdayStartTime) {
		this.weekdayStartTime = weekdayStartTime;
	}
	
	@MafsUISequence("7")
	public String getWeekdayEndTime() {
		return weekdayEndTime;
	}
	
	public void setWeekdayEndTime(String weekdayEndTime) {
		this.weekdayEndTime = weekdayEndTime;
	}
	
	@MafsUISequence("8")
	public String getSaturdayStartTime() {
		return saturdayStartTime;
	}
	
	public void setSaturdayStartTime(String saturdayStartTime) {
		this.saturdayStartTime = saturdayStartTime;
	}
	
	@MafsUISequence("9")
	public String getSaturdayEndTime() {
		return saturdayEndTime;
	}
	
	public void setSaturdayEndTime(String saturdayEndTime) {
		this.saturdayEndTime = saturdayEndTime;
	}
	
	@MafsUISequence("10")
	public String getSundayStartTime() {
		return sundayStartTime;
	}
	
	public void setSundayStartTime(String sundayStartTime) {
		this.sundayStartTime = sundayStartTime;
	}
	
	@MafsUISequence("11")
	public String getSundayEndTime() {
		return sundayEndTime;
	}
	
	public void setSundayEndTime(String sundayEndTime) {
		this.sundayEndTime = sundayEndTime;
	}
	
	@MafsUISequence("12")
	public Long getNumberOfBays() {
		return numberOfBays;
	}
	
	public void setNumberOfBays(Long numberOfBays) {
		this.numberOfBays = numberOfBays;
	}
	
	@MafsUISequence("13")
	public Long getClearanceInFeet() {
		return clearanceInFeet;
	}
	
	public void setClearanceInFeet(Long clearanceInFeet) {
		this.clearanceInFeet = clearanceInFeet;
	}
	
	@MafsUISequence("14")
	public String getAddressLine1() {
		return addressLine1;
	}
	
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	
	@MafsUISequence("15")
	public String getAddressLine2() {
		return addressLine2;
	}
	
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	
	@MafsUISequence("16")
	public String getAddressLine3() {
		return addressLine3;
	}
	
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}
	
	@MafsUISequence("17")
	public String getAddressLine4() {
		return addressLine4;
	}
	
	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}
	
	@MafsUISequence("18")
	public String getZipCode() {
		return zipCode;
	}
	
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	@MafsUISequence("19")
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	@MafsUISequence("20")
	public String getStateProv() {
		return stateProv;
	}
	
	public void setStateProv(String stateProv) {
		this.stateProv = stateProv;
	}
	
	@MafsUISequence("21")
	public String getCountyCode() {
		return countyCode;
	}
	
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryCode() {
		return this.countryCode;
	}	
	
	public String getOperationCode() {
		return operationCode;
	}
	
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
}
