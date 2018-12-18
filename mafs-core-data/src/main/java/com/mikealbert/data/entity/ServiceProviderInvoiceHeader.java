package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the MT_IF_HEADER database table.
 * 
 */
@Entity
@Table(name="MT_IF_HEADER")
public class ServiceProviderInvoiceHeader implements Serializable {
	private static final long serialVersionUID = -3140828993436116656L;

	@Id
	@Column(name="RECORD_ID")	
	private Long recordId;
	
	@Column(name="LOAD_ID")	
	private Long loadId;
	@Temporal(TemporalType.DATE)
	@Column(name="LOAD_DATE")
	private Date loadDate;
	@Column(name="LINE_COUNT")	
	private Long lineCount;
	@Column(name="RECORD_TYPE")
	private String recordType = "H";
	
	
	@Column(name="ACTION_TYPE")
	private String actionType;
	@Column(name="AP_IND")
	private String apInd;
	@Column(name="AR_IND")
	private String arInd;
	@Column(name="VENDOR_ID")
	private String parentProviderNumber;
	@Column(name="BRANCH_ID")
	private String serviceProviderNumber;
	@Temporal(TemporalType.DATE)
	@Column(name="DOC_DATE")
	private Date docDate;
	@Column(name="DOC_NO")
	private String docNo;
	@Column(name="DOC_TYPE")
	private String docType;
	@Column(name="DRIVER")
	private String driver;
	@Column(name="JOB_NO")
	private String jobNo;
	
	private Long mileage;
	@Column(name="ORIG_DOC_NO")
	private String origDocNo;
	@Temporal(TemporalType.DATE)
	@Column(name="PLANNED_START")
	private Date plannedStart;
	@Column(name="REG_NO")	
	private String regNo;
	@Column(name="UNIT_NO")
	private String unitNo;
	@Column(name="VALIDATE_IND")
	private String validateInd;
	@Column(name="VENDOR_REF")
	private String vendorRef;
	@Column(name="VIN")
	private String vin;
	
    @OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ServiceProviderInvoiceDetail> details;

	public ServiceProviderInvoiceHeader() {
	}
	
	
	public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public Long getLoadId() {
		return loadId;
	}
	public void setLoadId(Long loadId) {
		this.loadId = loadId;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getActionType() {
		return this.actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getApInd() {
		return this.apInd;
	}
	public void setApInd(String apInd) {
		this.apInd = apInd;
	}
	public String getArInd() {
		return this.arInd;
	}
	public void setArInd(String arInd) {
		this.arInd = arInd;
	}
	public String getParentProviderNumber() {
		return this.parentProviderNumber;
	}
	public void setParentProviderNumber(String parentProviderNumber) {
		this.parentProviderNumber = parentProviderNumber;
	}
	public String getServiceProviderNumber() {
		return this.serviceProviderNumber;
	}
	public void setServiceProviderNumber(String serviceProviderNumber) {
		this.serviceProviderNumber = serviceProviderNumber;
	}
	public Date getDocDate() {
		return this.docDate;
	}
	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}
	public String getDocNo() {
		return this.docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	public String getDocType() {
		return this.docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getDriver() {
		return this.driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getJobNo() {
		return this.jobNo;
	}
	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}



	public Long getLineCount() {
		return this.lineCount;
	}

	public void setLineCount(Long lineCount) {
		this.lineCount = lineCount;
	}



	public Date getLoadDate() {
		return this.loadDate;
	}

	public void setLoadDate(Date loadDate) {
		this.loadDate = loadDate;
	}

	public Long getMileage() {
		return this.mileage;
	}

	public void setMileage(Long mileage) {
		this.mileage = mileage;
	}



	public String getOrigDocNo() {
		return this.origDocNo;
	}

	public void setOrigDocNo(String origDocNo) {
		this.origDocNo = origDocNo;
	}



	public Date getPlannedStart() {
		return this.plannedStart;
	}

	public void setPlannedStart(Date plannedStart) {
		this.plannedStart = plannedStart;
	}

//	public String getRecordType() {
//		return this.recordType;
//	}

	public String getRegNo() {
		return this.regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}



	public String getUnitNo() {
		return this.unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}



	public String getValidateInd() {
		return this.validateInd;
	}

	public void setValidateInd(String validateInd) {
		this.validateInd = validateInd;
	}


	public String getVendorRef() {
		return this.vendorRef;
	}

	public void setVendorRef(String vendorRef) {
		this.vendorRef = vendorRef;
	}


	public String getVin() {
		return this.vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public List<ServiceProviderInvoiceDetail> getDetails() {
		return this.details;
	}

	public void setDetails(List<ServiceProviderInvoiceDetail> details) {
		this.details = details;
	}

//TODO: if we need this fix it!	
//	public ServiceProviderInvoiceDetail addServiceProviderInvoiceDetails(ServiceProviderInvoiceDetail serviceProviderInvoiceDetail) {
//		getMaintenanceInvoiceInterfaceDetails().add(serviceProviderInvoiceDetail);
//		serviceProviderInvoiceDetails.setMaintenanceInvoiceInterfaceHeader(this);
//
//		return serviceProviderInvoiceDetails;
//	}
//
//	public ServiceProviderInvoiceDetail removeMaintenanceInvoiceInterfaceDetails(ServiceProviderInvoiceDetail serviceProviderInvoiceDetail) {
//		getMaintenanceInvoiceInterfaceDetails().remove(serviceProviderInvoiceDetail);
//		serviceProviderInvoiceDetail.setServiceProviderInvoideHeader(null);
//
//		return serviceProviderInvoiceDetail;
//	}
}