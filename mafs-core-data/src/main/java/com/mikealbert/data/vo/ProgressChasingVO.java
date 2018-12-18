package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProgressChasingVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long mrqId;
	private String poNumber;
	private BigDecimal supId;
	private String lastChangedBy;
	private Date lastChangedDate;
	private String serviceProviderName;
	private String serviceProviderNumber;
	private String serviceProviderDetails;	
	private String concatCategoryDescriptions;
	private Date actualStartDate;
	private int odo;
	private String maintRequestStatusCode;
	private String maintRequestStatusDescription;	
	private BigDecimal taskTotalCost;
	private BigDecimal rechargeTotal;
	private BigDecimal sumTaskTotalCost;
	private BigDecimal sumRechargeTotal;
	private String maintReqServiceProviderDetail;
	private Long fmsId;
	private String unitNo;
	private String clientAccountNumber;
	private String clientAccountName;
	private String activityNotesLastUpdatedBy;
	private Date activityNotesLastUpdatedDate;
	private String serviceProviderContactInfo;
	

	public Long getMrqId() {
		return mrqId;
	}

	public void setMrqId(Long mrqId) {
		this.mrqId = mrqId;
	}
	
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}	

	public BigDecimal getSupId() {
		return supId;
	}

	public void setSupId(BigDecimal supId) {
		this.supId = supId;
	}

	public String getLastChangedBy() {
		return lastChangedBy;
	}

	public void setLastChangedBy(String lastChangedBy) {
		this.lastChangedBy = lastChangedBy;
	}

	public Date getLastChangedDate() {
		return lastChangedDate;
	}

	public void setLastChangedDate(Date lastChangedDate) {
		this.lastChangedDate = lastChangedDate;
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

	public String getServiceProviderDetails() {
		return serviceProviderDetails;
	}
	
	public void setServiceProviderDetails(String serviceProviderDetails) {
		this.serviceProviderDetails = serviceProviderDetails;
	}

	public String getConcatCategoryDescriptions() {
		return concatCategoryDescriptions;
	}

	public void setConcatCategoryDescriptions(String concatCategoryDescriptions) {
		this.concatCategoryDescriptions = concatCategoryDescriptions;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public int getOdo() {
		return odo;
	}

	public void setOdo(int odo) {
		this.odo = odo;
	}

	public String getMaintRequestStatusDescription() {
		return maintRequestStatusDescription;
	}

	public void setMaintRequestStatusDescription(String maintRequestStatusDescription) {
		this.maintRequestStatusDescription = maintRequestStatusDescription;
	}

	public String getMaintRequestStatusCode() {
		return maintRequestStatusCode;
	}

	public void setMaintRequestStatusCode(
			String maintRequestStatusCode) {
		this.maintRequestStatusCode = maintRequestStatusCode;
	}

	public BigDecimal getTaskTotalCost() {
		return taskTotalCost;
	}

	public void setTaskTotalCost(BigDecimal taskTotalCost) {
		this.taskTotalCost = taskTotalCost;
	}

	public BigDecimal getRechargeTotal() {
		return rechargeTotal;
	}

	public void setRechargeTotal(BigDecimal rechargeTotal) {
		this.rechargeTotal = rechargeTotal;
	}

	public BigDecimal getSumTaskTotalCost() {
		return sumTaskTotalCost;
	}

	public void setSumTaskTotalCost(BigDecimal sumTaskTotalCost) {
		this.sumTaskTotalCost = sumTaskTotalCost;
	}

	public BigDecimal getSumRechargeTotal() {
		return sumRechargeTotal;
	}

	public void setSumRechargeTotal(BigDecimal sumRechargeTotal) {
		this.sumRechargeTotal = sumRechargeTotal;
	}

	public String getMaintReqServiceProviderDetail() {
		return maintReqServiceProviderDetail;
	}

	public void setMaintReqServiceProviderDetail(
			String maintReqServiceProviderDetail) {
		this.maintReqServiceProviderDetail = maintReqServiceProviderDetail;
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

	public String getActivityNotesLastUpdatedBy() {
		return activityNotesLastUpdatedBy;
	}

	public void setActivityNotesLastUpdatedBy(String activityNotesLastUpdatedBy) {
		this.activityNotesLastUpdatedBy = activityNotesLastUpdatedBy;
	}

	public Date getActivityNotesLastUpdatedDate() {
		return activityNotesLastUpdatedDate;
	}

	public void setActivityNotesLastUpdatedDate(
			Date activityNotesLastUpdatedDate) {
		this.activityNotesLastUpdatedDate = activityNotesLastUpdatedDate;
	}

	public String getServiceProviderContactInfo() {
		return serviceProviderContactInfo;
	}

	public void setServiceProviderContactInfo(String serviceProviderContactInfo) {
		this.serviceProviderContactInfo = serviceProviderContactInfo;
	}

}

