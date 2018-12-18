package com.mikealbert.data.vo;

import java.util.Date;

import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.util.MALUtilities;

//TODO: consider moving this down to Vision; it is pretty specific to the vehicle search page
public class VehicleSearchResultVO {
	
	private static final String IN_STOCK = "Stock";
	private static final String ON_ORDER = "Vehicle On Order";
	private static final String PENDING_LIVE = "Pending Live";
	private static final String ON_CONTRACT = "On Contract";
	private static final String AWAITING_DISPOSAL = "Awaiting Disposal";
	private static final String DISPOSED_OF = "Disposed Of";
	private static final String OUT_OF_SERVICE = "Out Of Service";
	private static final String TERMINATED = "Terminated";
	
	//Unit Information: 1st Column
	private Long fmsId;
	private String unitNo;
	private String unitDescription;
	private String VIN;
	private String licensePlateNo;
	private String unitStatus;	
	private String clientFleetReferenceNumber;
	
	//Action Items: 2nd Column
	private int numOfOpenMaintPOs;
	
	//Driver Information: 3rd Column
	private Long drvId;
	private String driverForeName;
	private String driverSurname;
	private boolean driverAddressBusinessIndicator;
	private String driverBusinessAddressLine;
	private String driverAddress1;
	private String driverAddress2;
	private String driverCity;
	private String driverCounty;
	private String driverState;
	private String driverZip;
	private String driverAreaCode;
	private String driverPhoneNumber;
	private String driverPhoneExtension;
	private String driverEmail;
	private String driverStatus;
	private boolean driverPoolManager;
	private boolean driverActive;
	private String driverFullNameDisplay;
	
	//Unit's Client Information: 4th Column
	private String clientAccountNumber;
	private String clientAccountName;
	private String clientAccountType;
	private Long clientCorpEntity;

	//Misc: 5th Column
	//TODO: Add fields for PO NO, Vendor Invoice No, and MAFS (Internal) Invoice No. These field will be populated based on their respective search criteria 
	private Long maintenanceRequestId;
	private String purchaseOrderNumber;
	private String serviceProviderNumber;
	private String serviceProviderName;
	private String serviceProviderInvoiceNumber;
	private String internalInvoiceNumber;
	private Date contractStartDate;
	private Date contractEndDate;
	private Date inServiceDate;
	private Date contractOutOfServiceDate;
	private Date contractActualEndDate;
	private Date unitStatusDate;
	
	//Search Contract Vehicles page fields
	private String productName;
	private Long qmdId;
	private Long contractTerm;
	private Long contractDistance;
	private boolean vehicleUnderMaintenanceFlag;

		
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

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String vIN) {
		VIN = vIN;
	}

	public String getLicensePlateNo() {
		return licensePlateNo;
	}

	public void setLicensePlateNo(String licensePlateNo) {
		this.licensePlateNo = licensePlateNo;
	}

	public String getUnitStatus() {
		return unitStatus;
	}

	public void setUnitStatus(String unitStatus) {
		this.unitStatus = unitStatus;
	}

	public String getClientFleetReferenceNumber() {
		return clientFleetReferenceNumber;
	}

	public void setClientFleetReferenceNumber(String clientFleetReferenceNumber) {
		this.clientFleetReferenceNumber = clientFleetReferenceNumber;
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

	public boolean isDriverAddressBusinessIndicator() {
		return driverAddressBusinessIndicator;
	}

	public void setDriverAddressBusinessIndicator(
			boolean driverAddressBusinessIndicator) {
		this.driverAddressBusinessIndicator = driverAddressBusinessIndicator;
	}

	public String getDriverBusinessAddressLine() {
		return driverBusinessAddressLine;
	}

	public void setDriverBusinessAddressLine(String driverBusinessAddressLine) {
		this.driverBusinessAddressLine = driverBusinessAddressLine;
	}

	public String getDriverAddress1() {
		return driverAddress1;
	}

	public void setDriverAddress1(String driverAddress1) {
		this.driverAddress1 = driverAddress1;
	}

	public String getDriverAddress2() {
		return driverAddress2;
	}

	public void setDriverAddress2(String driverAddress2) {
		this.driverAddress2 = driverAddress2;
	}

	public String getDriverCity() {
		return driverCity;
	}

	public void setDriverCity(String driverCity) {
		this.driverCity = driverCity;
	}

	public String getDriverCounty() {
		return driverCounty;
	}

	public void setDriverCounty(String driverCounty) {
		this.driverCounty = driverCounty;
	}

	public String getDriverState() {
		return driverState;
	}

	public void setDriverState(String driverState) {
		this.driverState = driverState;
	}

	public String getDriverZip() {
		return driverZip;
	}

	public void setDriverZip(String driverZip) {
		this.driverZip = driverZip;
	}

	public String getDriverAreaCode() {
		return driverAreaCode;
	}

	public void setDriverAreaCode(String driverAreaCode) {
		this.driverAreaCode = driverAreaCode;
	}

	public String getDriverPhoneNumber() {
		return driverPhoneNumber;
	}

	public void setDriverPhoneNumber(String driverPhoneNumber) {
		this.driverPhoneNumber = driverPhoneNumber;
	}

	public String getDriverPhoneExtension() {
		return driverPhoneExtension;
	}

	public void setDriverPhoneExtension(String driverPhoneExtension) {
		this.driverPhoneExtension = driverPhoneExtension;
	}

	public String getDriverEmail() {
		return driverEmail;
	}

	public void setDriverEmail(String driverEmail) {
		this.driverEmail = driverEmail;
	}

	public String getDriverStatus() {
		return driverStatus;
	}

	public void setDriverStatus(String driverStatus) {
		this.driverStatus = driverStatus;
	}

	public boolean isDriverPoolManager() {
		return driverPoolManager;
	}

	public void setDriverPoolManager(boolean driverPoolManager) {
		this.driverPoolManager = driverPoolManager;
	}

	public boolean isDriverActive() {
		return driverActive;
	}
	
	public void setDriverActive(boolean driverActive) {
		this.driverActive = driverActive;
	}
	
	public String getClientAccountNumber() {
		return clientAccountNumber;
	}

	public void setClientAccountNumber(String clientAccountNumber) {
		this.clientAccountNumber = clientAccountNumber;
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

	public Long getClientCorpEntity() {
		return clientCorpEntity;
	}

	public void setClientCorpEntity(Long clientCorpEntity) {
		this.clientCorpEntity = clientCorpEntity;
	}

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public Long getMaintenanceRequestId() {
		return maintenanceRequestId;
	}
	public void setMaintenanceRequestId(Long maintenanceRequestId) {
		this.maintenanceRequestId = maintenanceRequestId;
	}
	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public String getServiceProviderNumber() {
		return serviceProviderNumber;
	}
	public void setserviceProviderNumber(String serviceProviderNumber) {
		this.serviceProviderNumber = serviceProviderNumber;
	}
	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}

	public String getServiceProviderInvoiceNumber() {
		return serviceProviderInvoiceNumber;
	}

	public void setServiceProviderInvoiceNumber(String serviceProviderInvoiceNumber) {
		this.serviceProviderInvoiceNumber = serviceProviderInvoiceNumber;
	}
	
	public String getInternalInvoiceNumber() {
		return internalInvoiceNumber;
	}
	
	public void setInternalInvoiceNumber(String internalInvoiceNumber) {
		this.internalInvoiceNumber = internalInvoiceNumber;
	}
	
	public Date getContractStartDate() {
		return contractStartDate;
	}
	
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	
	public Date getInServiceDate() {
		return inServiceDate;
	}
	
	public void setInServiceDate(Date inServiceDate) {
		this.inServiceDate = inServiceDate;
	}
	
	public Date getContractActualEndDate() {
		return contractActualEndDate;
	}
	
	public void setContractActualEndDate(Date contractActualEndDate) {
		this.contractActualEndDate = contractActualEndDate;
	}
	
	public String getDriverFullNameDisplay() {
		return DisplayFormatHelper.formatDriverForTable(driverForeName, driverSurname, driverPoolManager, "<br/>");
	}
	
	public Date getContractOutOfServiceDate() {
		return contractOutOfServiceDate;
	}
	public void setContractOutOfServiceDate(Date contractOutOfServiceDate) {
		this.contractOutOfServiceDate = contractOutOfServiceDate;
	}
	public Date getUnitStatusDate() {
		Date unitStatusDate;
		
		//If in Stock, then no date
		if(unitStatus.equalsIgnoreCase(IN_STOCK)){
			unitStatusDate = null;
		}else if(unitStatus.equalsIgnoreCase(ON_ORDER)) { //If Vehicle On Order, then no date
			unitStatusDate = null;
		} else if(unitStatus.equalsIgnoreCase(PENDING_LIVE)) { //If Pending Live, then the In Service Date
			unitStatusDate = inServiceDate;
		} else if(unitStatus.equalsIgnoreCase(ON_CONTRACT)) { //If On Contract, then the current Contract Start Date
			//(If the contract start date is a future date, the vehicle is considered Pending Live or In Service)
			if(MALUtilities.isFutureDate(contractStartDate)){
				unitStatusDate = inServiceDate;
			}else{
				unitStatusDate = contractStartDate;
			}
		} else if(unitStatus.equalsIgnoreCase(OUT_OF_SERVICE) || unitStatus.equalsIgnoreCase(AWAITING_DISPOSAL)) { //If Out of Service or Awaiting Disposal, then the Out of Service Date
			unitStatusDate = contractOutOfServiceDate;
		} else if(unitStatus.equalsIgnoreCase(DISPOSED_OF) || unitStatus.equalsIgnoreCase(TERMINATED)){ //If Disposed of or Terminated, then the Actual Contract Termination Date 
			//if the Act Cont Term date is a future date, the vehicle is considered Out of Service)
			if(MALUtilities.isFutureDate(contractActualEndDate)){
				unitStatusDate = contractOutOfServiceDate;
			}else{
				unitStatusDate = contractActualEndDate;
			}
		} else {
			 unitStatusDate = null;
		}
		
		return unitStatusDate;
	}
	
	public String getClientForDisplay() {
		return DisplayFormatHelper.formatClientForTable(this.clientAccountName, this.clientAccountNumber, "<br/>");
	}
	
	public String getDriverAddressDisplay() {
		return DisplayFormatHelper.formatAddressForTable(this.driverBusinessAddressLine, this.driverAddress1, this.driverAddress2, null, null, this.driverCity, this.driverState, this.driverZip, "<br/>");
	}
	
	public String getDriverPhoneDisplay() {
		return DisplayFormatHelper.formatPhoneNumberForTable(this.driverAreaCode, this.driverPhoneNumber, this.driverPhoneExtension, "<br/>");
	}
	
	public int getNumOfOpenMaintPOs() {
		return numOfOpenMaintPOs;
	}
	public void setNumOfOpenMaintPOs(int numOfOpenMaintPOs) {
		this.numOfOpenMaintPOs = numOfOpenMaintPOs;
	}
	public Date getContractEndDate() {
		return contractEndDate;
	}
	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Long getQmdId() {
		return qmdId;
	}
	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}
	public Long getContractTerm() {
		return contractTerm;
	}
	public void setContractTerm(Long contractTerm) {
		this.contractTerm = contractTerm;
	}
	public Long getContractDistance() {
		return contractDistance;
	}
	public void setContractDistance(Long contractDistance) {
		this.contractDistance = contractDistance;
	}
	public boolean isVehicleUnderMaintenanceFlag() {
		return vehicleUnderMaintenanceFlag;
	}
	public void setVehicleUnderMaintenanceFlag(boolean vehicleUnderMaintenanceFlag) {
		this.vehicleUnderMaintenanceFlag = vehicleUnderMaintenanceFlag;
	}
	public boolean getVehicleUnderMaintenanceFlag() {
		return vehicleUnderMaintenanceFlag;
	}
}