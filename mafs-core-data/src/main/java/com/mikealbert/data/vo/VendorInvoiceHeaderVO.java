package com.mikealbert.data.vo;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class VendorInvoiceHeaderVO implements Serializable {

	private static final long serialVersionUID = -8715923994927048069L;
	
	private String messageId;
	private String parentProvider;
	private String recordId;
	private String loadId;
	
	private String recordType;
	private String unitNo;
	private String poNbr;
	private String storeNbr;
	private String docNumber;
	private String driver;
	private String vendorRef;
	
	private String vin;
	private String parentNbr;
	private String plannedDate;
	private String mileage;
	private String docType;
	private String docDate;
	private String origDocNo;
	private String plateNo;
	
	private List<VendorInvoiceDetailsVO> details;
	
	private List<VendorInvoiceErrorsVO> errors;
	
	public VendorInvoiceHeaderVO(){
		details = new ArrayList<VendorInvoiceDetailsVO>();
		errors = new ArrayList<VendorInvoiceErrorsVO>();
	}
	
	
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
	
	
	
	public String getLoadId() {
		return loadId;
	}


	public void setLoadId(String loadId) {
		this.loadId = loadId;
	}


	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getPoNbr() {
		return poNbr;
	}
	public void setPoNbr(String poNbr) {
		this.poNbr = poNbr;
	}
	public String getStoreNbr() {
		return storeNbr;
	}
	public void setStoreNbr(String storeNbr) {
		this.storeNbr = storeNbr;
	}
	public String getDocNumber() {
		return docNumber;
	}
	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getVendorRef() {
		return vendorRef;
	}
	public void setVendorRef(String vendorRef) {
		this.vendorRef = vendorRef;
	}
	
	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getParentNbr() {
		return parentNbr;
	}
	public void setParentNbr(String parentNbr) {
		this.parentNbr = parentNbr;
	}
	public String getPlannedDate() {
		return plannedDate;
	}
	public void setPlannedDate(String plannedDate) {
		this.plannedDate = plannedDate;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getDocDate() {
		return docDate;
	}
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}
	public String getOrigDocNo() {
		return origDocNo;
	}
	public void setOrigDocNo(String origDocNo) {
		this.origDocNo = origDocNo;
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}


	public List<VendorInvoiceDetailsVO> getDetails() {
		return details;
	}


	public void setDetails(List<VendorInvoiceDetailsVO> details) {
		this.details = details;
	}
	
	public void addDetail(VendorInvoiceDetailsVO detail) {
		if (details == null) {
			details = new ArrayList<VendorInvoiceDetailsVO>();
		}
		
		details.add(detail);
	}


	public List<VendorInvoiceErrorsVO> getErrors() {
		return errors;
	}


	public void setErrors(List<VendorInvoiceErrorsVO> errors) {
		this.errors = errors;
	}
	
	
	
}
