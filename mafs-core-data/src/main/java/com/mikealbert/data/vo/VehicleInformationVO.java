package com.mikealbert.data.vo;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.VehicleTechnicalInfo;
import com.mikealbert.data.entity.WarrantyUnitLink;
import com.mikealbert.util.MALUtilities;

public class VehicleInformationVO {
	//Unit Specific Info
	private Long fmsId;
	private String unitNo;
	private String unitDescription;
	private String vin;
	private String licensePlateNo;
	private String clientFleetReferenceNumber;
	private String productType;
	private String status;	
	private String replacementUnitNo;
	private Date replacementUnitDate;
	private String replacementUnitDateType;
	private Long qmdId;
	
	//Driver specific Info
	private Long drvId;
	private String driverForeName;
	private String driverSurname;
	private boolean driverPoolManager;
	private String driverFullNameDisplay;
	private String driverCostCenter;
	private String driverCostCenterName;
	
	//Unit's Client Information: 3rd Column
	private String clientAccountNumber;
	private String clientAccountName;
	private String clientAccountType;
	private Long clientCorporateId;
	private String clientTaxIndicator;
	
    //Contract specific info 
	private Long clnId;
	private Date contractStartDate;
	private Date contractEndDate;
	private Date contractActualEndDate;
	
	//Vehicle Technical Information
	private VehicleTechnicalInfo vehicleTechInfo;
	
	//Extended Warranty Indicator
	private List<WarrantyUnitLink> warrantyUnitLinks;
		
	public Long getFmsId() {
		return fmsId;
	}
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getUnitDescription() {
		return unitDescription;
	}

	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}

	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getLicensePlateNo() {
		return licensePlateNo;
	}

	public void setLicensePlateNo(String licensePlateNo) {
		this.licensePlateNo = licensePlateNo;
	}

	public String getClientFleetReferenceNumber() {
		return clientFleetReferenceNumber;
	}

	public void setClientFleetReferenceNumber(String clientFleetReferenceNumber) {
		this.clientFleetReferenceNumber = clientFleetReferenceNumber;
	}

	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReplacementUnitNo() {
		return replacementUnitNo;
	}
	public void setReplacementUnitNo(String replacementUnitNo) {
		this.replacementUnitNo = replacementUnitNo;
	}
	public Date getReplacementUnitDate() {
		return replacementUnitDate;
	}
	public void setReplacementUnitDate(Date replacementUnitDate) {
		this.replacementUnitDate = replacementUnitDate;
	}
	public String getReplacementUnitDateType() {
		return replacementUnitDateType;
	}
	public void setReplacementUnitDateType(String replacementUnitDateType) {
		this.replacementUnitDateType = replacementUnitDateType;
	}
	public Long getDrvId() {
		return drvId;
	}

	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}

	public String getDriverForeName() {
		return driverForeName;
	}

	public void setDriverForeName(String driverForeName) {
		this.driverForeName = driverForeName;
	}

	public String getDriverSurname() {
		return driverSurname;
	}

	public void setDriverSurname(String driverSurname) {
		this.driverSurname = driverSurname;
	}

	public boolean isDriverPoolManager() {
		return driverPoolManager;
	}

	public void setDriverPoolManager(boolean driverPoolManager) {
		this.driverPoolManager = driverPoolManager;
	}
	
	public String getClientAccountName() {
		return clientAccountName;
	}

	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName;
	}

	public String getClientAccountType() {
		return clientAccountType;
	}

	public void setClientAccountType(String clientAccountType) {
		this.clientAccountType = clientAccountType;
	}
	
	public Long getClientCorporateId() {
		return clientCorporateId;
	}
	public void setClientCorporateId(Long clientCorporateId) {
		this.clientCorporateId = clientCorporateId;
	}
	public Long getClnId() {
		return clnId;
	}
	public void setClnId(Long clnId) {
		this.clnId = clnId;
	}
	public Date getContractStartDate() {
		return contractStartDate;
	}
	
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	
	public Date getContractEndDate() {
		return contractEndDate;
	}
	
	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	
	public Date getContractActualEndDate() {
		return contractActualEndDate;
	}
	
	public void setContractActualEndDate(Date contractActualEndDate) {
		this.contractActualEndDate = contractActualEndDate;
	}
	
	public String getDriverFullNameDisplay() {
		if(!(MALUtilities.isEmptyString(getDriverSurname()) && MALUtilities.isEmptyString(getDriverForeName()))){
			this.driverFullNameDisplay = getDriverSurname() + ", " + getDriverForeName();
		} 
		
		if(isDriverPoolManager()){
			this.driverFullNameDisplay += " (Pool Manager)";
		}
		
		return this.driverFullNameDisplay;
	}
	
	public void setDriverFullNameDisplay(String driverFullNameDisplay) {
		this.driverFullNameDisplay = driverFullNameDisplay;
	}
	public String getDriverCostCenter() {
		return driverCostCenter;
	}
	public void setDriverCostCenter(String driverCostCenter) {
		this.driverCostCenter = driverCostCenter;
	}
	public String getClientAccountNumber() {
		return clientAccountNumber;
	}
	public void setClientAccountNumber(String clientAccountNumber) {
		this.clientAccountNumber = clientAccountNumber;
	}
	public String getClientTaxIndicator() {
		return clientTaxIndicator;
	}
	public void setClientTaxIndicator(String clientTaxIndicator) {
		this.clientTaxIndicator = clientTaxIndicator;
	}
	
	public VehicleTechnicalInfo getVehicleTechInfo() {
		return vehicleTechInfo;
	}
	
	public void setVehicleTechInfo(VehicleTechnicalInfo vehicleTechInfo) {
		this.vehicleTechInfo = vehicleTechInfo;
	}
	public List<WarrantyUnitLink> getWarrantyUnitLinks() {
		return warrantyUnitLinks;
	}
	public void setWarrantyUnitLinks(List<WarrantyUnitLink> warrantyUnitLinks) {
		this.warrantyUnitLinks = warrantyUnitLinks;
	}
	public String getDriverCostCenterName() {
		return driverCostCenterName;
	}
	public void setDriverCostCenterName(String driverCostCenterName) {
		this.driverCostCenterName = driverCostCenterName;
	}
	public Long getQmdId() {
		return qmdId;
	}
	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}
}
