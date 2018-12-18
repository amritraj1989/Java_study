package com.mikealbert.data.vo;

import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.util.MALUtilities;
// TODO: consider moving this down to Vision; it is pretty specific to the vehicle search page
public class VehicleSearchCriteriaVO {
	private String unitNo;
	private String driverName;
	private String driverId; // used to store the drv id of a driver that is selected by the LOV
	private String VIN;
	private String licensePlateNo;
	private String clientFleetReferenceNumber;
	private String clientAccountNumber;
	private String clientAccountName;
	private String purchaseOrderNumber;
	private String serviceProviderName;
	private String serviceProviderInvoiceNumber;
	private String internalnvoiceNumber;
	private CorporateEntity corporateEntity;
	private String unitStatus = "A";
	private	String	vehSchSeq;
	
	private boolean contractVehicleSearch = false;
	
	public VehicleSearchCriteriaVO(){}
	
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo.trim();
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName.trim();
	}
	public String getVIN() {
		return VIN;
	}
	public void setVIN(String vIN) {
		VIN = vIN.trim();
	}
	public String getLicensePlateNo() {
		return licensePlateNo;
	}
	public void setLicensePlateNo(String licensePlateNo) {
		this.licensePlateNo = licensePlateNo.trim();
	}
	public String getClientFleetReferenceNumber() {
		return clientFleetReferenceNumber;
	}
	public void setClientFleetReferenceNumber(String clientFleetReferenceNumber) {
		this.clientFleetReferenceNumber = clientFleetReferenceNumber.trim();
	}

	public String getClientAccountNumber() {
		return clientAccountNumber;
	}

	public void setClientAccountNumber(String clientAccountNumber) {
		this.clientAccountNumber = clientAccountNumber.trim();
	}

	public String getClientAccountName() {
		return clientAccountName;
	}

	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName.trim();
	}

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}
	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber.trim();
	}

	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName.trim();
	}

	public String getServiceProviderInvoiceNumber() {
		return serviceProviderInvoiceNumber;
	}
	public void setServiceProviderInvoiceNumber(String serviceProviderInvoiceNumber) {
		this.serviceProviderInvoiceNumber = serviceProviderInvoiceNumber.trim();
	}
	public String getInternalnvoiceNumber() {
		return internalnvoiceNumber;
	}
	public CorporateEntity getCorporateEntity() {
		return corporateEntity;
	}

	public void setCorporateEntity(CorporateEntity corporateEntity) {
		this.corporateEntity = corporateEntity;
	}

	public void setInternalnvoiceNumber(String internalnvoiceNumber) {
		this.internalnvoiceNumber = internalnvoiceNumber.trim();
	}

	public String getDriverForeName(){
		String foreName = null;
		if(!MALUtilities.isEmpty(getDriverName()) && getDriverName().split(",").length > 1){
			foreName = this.getDriverName().split(",")[1].trim();			
		}		
		return foreName;
	}
	
	public String getDriverSurName(){
		String surName = null;
		if(!MALUtilities.isEmpty(getDriverName())){
			surName = getDriverName().split(",")[0].trim();			
		}		
		return surName;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getUnitStatus() {
		return unitStatus;
	}

	public void setUnitStatus(String unitStatus) {
		this.unitStatus = unitStatus;
	}
	

	public String getVehSchSeq() {
		return vehSchSeq;
	}

	public void setVehSchSeq(String vehSchSeq) {
		if(!MALUtilities.isEmpty(vehSchSeq)){
			vehSchSeq = vehSchSeq.trim();
		}
		this.vehSchSeq = vehSchSeq;
	}

	public boolean isContractVehicleSearch() {
		return contractVehicleSearch;
	}

	public void setContractVehicleSearch(boolean contractVehicleSearch) {
		this.contractVehicleSearch = contractVehicleSearch;
	}

	@Override
	public String toString() {
		
		return "VehicleSearchCriteriaVO [unitNo=" + unitNo + ", driverName="
				+ driverName + ", driverId=" + driverId + ", VIN=" + VIN
				+ ", licensePlateNo=" + licensePlateNo
				+ ", clientFleetReferenceNumber=" + clientFleetReferenceNumber
				+ ", clientAccountNumber=" + clientAccountNumber
				+ ", clientAccountName=" + clientAccountName
				+ ", purchaseOrderNumber=" + purchaseOrderNumber
				+ ", serviceProviderName=" + serviceProviderName
				+ ", serviceProviderInvoiceNumber="+ serviceProviderInvoiceNumber 
				+ ", internalnvoiceNumber="	+ internalnvoiceNumber 	+ ", unitStatus=" + unitStatus + "]";
	}
	
}
