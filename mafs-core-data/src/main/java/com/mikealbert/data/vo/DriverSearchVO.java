package com.mikealbert.data.vo;

import java.util.Date;

import com.mikealbert.util.MALUtilities;

public class DriverSearchVO {
	
	public static  String  FIELD_DRIVER_NAME = "driverForename";
	public static  String  FIELD_ACCOUNT_NAME = "accountName";
	public static  String  FIELD_UNIT_NO = "unitNo";
	public static  String  FIELD_VEHICLE_STATUS = "vehicleStatus";
	
	private Long drvId;	
	private String driverForename;
	private String driverSurname;
	private String driverEmail;
	private String activeInd;
	private String driverPhone;	
	private String townCity;
	private String region;	
	private String postcode;	
	private String businessaddressLine;	
	private String addressLine1;	
	private String addressLine2;
	private String addressLine;
	private Date contractLineStartDate;	
	private Date contractLineEndDate;
	private String accountName;
	private String accountCode;
	private String unitNo;
	private Long fmsId;
	private String modelDesc;	
	private String vin;	
	private String vehicleStatus;
	private String  uniqueRowId;
	private String  fleetRefNo;
	private String  licensePlate;
	private String poolManager;
	private String costCentre;
	
	public String getUniqueRowId() {		
		uniqueRowId = String.valueOf(drvId);			
			if(unitNo != null){
				uniqueRowId = uniqueRowId +"_" +unitNo;
		 }		 
		return uniqueRowId;
	}	
	public Long getDrvId() {
		return drvId;
	}
	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}
	public String getDriverForename() {
		return driverForename;
	}
	public void setDriverForename(String driverForename) {
		this.driverForename = driverForename;
	}
	public String getDriverSurname() {
		return driverSurname;
	}
	public void setDriverSurname(String driverSurname) {
		this.driverSurname = driverSurname;
	}
	public String getDriverEmail() {
		return driverEmail;
	}
	public void setDriverEmail(String driverEmail) {
		this.driverEmail = driverEmail;
	}
	
	public String getActiveInd() {
		return activeInd;
	}
	public void setActiveInd(String activeInd) {
		this.activeInd = activeInd;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public String getBusinessaddressLine() {
		return businessaddressLine;
	}
	public void setBusinessaddressLine(String businessaddressLine) {
		this.businessaddressLine = businessaddressLine;
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
	public Date getContractLineStartDate() {
		return contractLineStartDate;
	}
	public void setContractLineStartDate(Date contractLineStartDate) {
		this.contractLineStartDate =  contractLineStartDate;
	}
	public Date getContractLineEndDate() {
		return contractLineEndDate;
	}
	public void setContractLineEndDate(Date contractLineEndDate) {
		this.contractLineEndDate =  contractLineEndDate;
	}
	public boolean isHasUnit() {
		return (!MALUtilities.isEmpty(fmsId));
	}
	public boolean isHasEndDate() {
		return (!MALUtilities.isEmpty(contractLineEndDate));
	}
	
	public String getTownCity() {
		StringBuilder townCityFormattedBuilder = new StringBuilder();
		String[] cityParts = townCity.split(" ");
		for(String part : cityParts){
			townCityFormattedBuilder.append(part.substring(0, 1).toUpperCase());
			townCityFormattedBuilder.append(part.substring(1, part.length()).toLowerCase());
			townCityFormattedBuilder.append(" ");
		}
		townCityFormattedBuilder.deleteCharAt(townCityFormattedBuilder.length() - 1);
		
		return townCityFormattedBuilder.toString();
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
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public Long getFmsId() {
		return fmsId;
	}
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}
	public String getModelDesc() {
		return modelDesc;
	}
	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getVehicleStatus() {		
		return vehicleStatus;
	}
	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}
	public String getFleetRefNo() {
		return fleetRefNo;
	}
	public void setFleetRefNo(String fleetRefNo) {
		this.fleetRefNo = fleetRefNo;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	public String getPoolManager() {
		return poolManager;
	}
	public void setPoolManager(String poolManager) {
		this.poolManager = poolManager;
	}
	public String getCostCentre() {
		return costCentre;
	}
	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}
	public boolean isHasCostCentre() {
		return (!MALUtilities.isEmpty(costCentre));
	}
	
}
