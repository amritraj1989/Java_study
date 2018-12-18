package com.mikealbert.data.vo;

import java.util.Date;

public class MaintenanceRequestVO {
	Long maintReqId;
	Date maintReqDate;
	int currentOdo;
	int mostRecentOdo;
	Date mostRecentOdoDate;
	String maintReqStatus;
	String maintReqType;
	Date plannedStartDate;
	Date plannedEndDate;
	Date actualStartDate;
	Date actualEndDate;
	String maintReqJobNo;
	String uomCode;
	String uomDescription;
	String serviceProviderName;
	String serviceProviderNumber;
	String serviceProviderContactName;
	String serviceProviderInvoice;
	String payeeName;
	String payeeNumber;
	Long fmsId;
	
	public Long getMaintReqId() {
		return maintReqId;
	}
	public void setMaintReqId(Long maintReqId) {
		this.maintReqId = maintReqId;
	}
	public Date getMaintReqDate() {
		return maintReqDate;
	}
	public void setMaintReqDate(Date maintReqDate) {
		this.maintReqDate = maintReqDate;
	}
	public int getCurrentOdo() {
		return currentOdo;
	}
	public void setCurrentOdo(int currentOdo) {
		this.currentOdo = currentOdo;
	}
	public int getMostRecentOdo() {
		return mostRecentOdo;
	}
	public void setMostRecentOdo(int mostRecentOdo) {
		this.mostRecentOdo = mostRecentOdo;
	}
	public Date getMostRecentOdoDate() {
		return mostRecentOdoDate;
	}
	public void setMostRecentOdoDate(Date mostRecentOdoDate) {
		this.mostRecentOdoDate = mostRecentOdoDate;
	}
	public String getMaintReqStatus() {
		return maintReqStatus;
	}
	public void setMaintReqStatus(String maintReqStatus) {
		this.maintReqStatus = maintReqStatus;
	}
	public String getMaintReqType() {
		return maintReqType;
	}
	public void setMaintReqType(String maintReqType) {
		this.maintReqType = maintReqType;
	}
	public Date getPlannedStartDate() {
		return plannedStartDate;
	}
	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}
	public Date getPlannedEndDate() {
		return plannedEndDate;
	}
	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}
	public Date getActualStartDate() {
		return actualStartDate;
	}
	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}
	public Date getActualEndDate() {
		return actualEndDate;
	}
	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}
	public String getMaintReqJobNo() {
		return maintReqJobNo;
	}
	public void setMaintReqJobNo(String maintReqJobNo) {
		this.maintReqJobNo = maintReqJobNo;
	}
	public String getUomCode() {
		return uomCode;
	}
	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}
	public String getUomDescription() {
		return uomDescription;
	}
	public void setUomDescription(String uomDescription) {
		this.uomDescription = uomDescription;
	}
	public String getServiceProviderName() {
		return serviceProviderName;
	}
	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}
	public String getServiceProviderNumber() {
		return serviceProviderNumber;
	}
	public void setServiceProviderNumber(String serviceProviderNumber) {
		this.serviceProviderNumber = serviceProviderNumber;
	}
	public String getServiceProviderContactName() {
		return serviceProviderContactName;
	}
	public void setServiceProviderContactName(String serviceProviderContactName) {
		this.serviceProviderContactName = serviceProviderContactName;
	}
	public String getServiceProviderInvoice() {
		return serviceProviderInvoice;
	}
	public void setServiceProviderInvoice(String serviceProviderInvoice) {
		this.serviceProviderInvoice = serviceProviderInvoice;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeNumber() {
		return payeeNumber;
	}
	public void setPayeeNumber(String payeeNumber) {
		this.payeeNumber = payeeNumber;
	}
	public Long getFmsId() {
		return fmsId;
	}
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}
}
