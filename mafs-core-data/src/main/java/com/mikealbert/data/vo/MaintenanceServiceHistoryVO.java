package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.MaintenanceCode;

public class MaintenanceServiceHistoryVO implements Serializable {
	private static final long serialVersionUID = -8940867060627632836L;
	
	public static  String  FIELD_START_DATE = "actualStartDate";
	public static  String  FIELD_ODO = "odo";
	public static  String  FIELD_STATUS = "maintRequestStatus";
	public static  String  FILTER_SERVICE_PROVIDER = "concatServiceProviderNameAndNumber";
	public static  String  FILTER_CATEGORIES = "concatCategoryDescriptions";
	
	private Long mrqId;
	private String poNumber;
	private Date actualStartDate;
	private int odo;
	private BigDecimal supId;
	private String serviceProviderName;
	private String serviceProviderNumber;
	private String serviceProviderTelephone;
	private String maintRequestStatus;
	private BigDecimal doclTotalCost;
	private BigDecimal taskTotalCostRechN;
	private BigDecimal taskTotalCost;
	private BigDecimal totalCost;
	private BigDecimal rechargeTotal;
	private BigDecimal actualInvoice; // added for Bug 16387
	private BigDecimal actualCustCharge; // added for Bug 16387
	private String payeeInvoiceNumber;
	private Date payeeInvoiceDate;
	private String mafsInvoiceNumber;
	private Date mafsInvoiceDate;
	private String serviceProviderDetails;
	private  List<MaintenanceCode> listMaintenanceCodes;
	private boolean creditFlag;
	private String maintReqServiceProviderDetail;
	
	//Used for filtering
	private String concatCategoryDescriptions;
	private String concatServiceProviderNameAndNumber;
	
	//Used for sorting Maint Request Statuses in B, I, C order
	private int maintRequestStatusRank;
	
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
	public String getMaintRequestStatus() {
		return maintRequestStatus;
	}
	public void setMaintRequestStatus(String maintRequestStatus) {
		this.maintRequestStatus = maintRequestStatus;
	}
	public BigDecimal getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}
	public BigDecimal getRechargeTotal() {
		return rechargeTotal;
	}
	public void setRechargeTotal(BigDecimal rechargeTotal) {
		this.rechargeTotal = rechargeTotal;
	}
	public String getPayeeInvoiceNumber() {
		return payeeInvoiceNumber;
	}
	public void setPayeeInvoiceNumber(String payeeInvoiceNumber) {
		this.payeeInvoiceNumber = payeeInvoiceNumber;
	}
	public Date getPayeeInvoiceDate() {
		return payeeInvoiceDate;
	}
	public void setPayeeInvoiceDate(Date payeeInvoiceDate) {
		this.payeeInvoiceDate = payeeInvoiceDate;
	}
	public String getMafsInvoiceNumber() {
		return mafsInvoiceNumber;
	}
	public void setMafsInvoiceNumber(String mafsInvoiceNumber) {
		this.mafsInvoiceNumber = mafsInvoiceNumber;
	}
	public String getConcatServiceProviderNameAndNumber() {
		this.concatServiceProviderNameAndNumber = this.getServiceProviderName();
		if(this.getServiceProviderNumber() != null && !this.getServiceProviderNumber().isEmpty()){
			this.concatServiceProviderNameAndNumber = this.concatServiceProviderNameAndNumber +  " " + this.getServiceProviderNumber();
		}
		return this.concatServiceProviderNameAndNumber;
	}

	public String getConcatCategoryDescriptions() {
		return concatCategoryDescriptions;
	}
	public void setConcatCategoryDescriptions(String concatCategoryDescriptions) {
		this.concatCategoryDescriptions = concatCategoryDescriptions;
	}
	public  List<MaintenanceCode> getListMaintenanceCodes() {
		return listMaintenanceCodes;
	}
	public void setListMaintenanceCodes( List<MaintenanceCode> listMaintenanceCodes) {
		this.listMaintenanceCodes = listMaintenanceCodes;
	}
	public String getServiceProviderDetails() {
		return serviceProviderDetails;
	}
	public void setServiceProviderDetails(String serviceProviderDetails) {
		this.serviceProviderDetails = serviceProviderDetails;
	}
	public boolean isCreditFlag() {
		return creditFlag;
	}
	public void setCreditFlag(boolean creditFlag) {
		this.creditFlag = creditFlag;
	}
	public int getMaintRequestStatusRank() {
		return maintRequestStatusRank;
	}
	public void setMaintRequestStatusRank(int maintRequestStatusRank) {
		this.maintRequestStatusRank = maintRequestStatusRank;
	}
	public BigDecimal getSupId() {
		return supId;
	}
	public void setSupId(BigDecimal supId) {
		this.supId = supId;
	}
	public BigDecimal getDoclTotalCost() {
		return doclTotalCost;
	}
	public void setDoclTotalCost(BigDecimal doclTotalCost) {
		this.doclTotalCost = doclTotalCost;
	}
	public BigDecimal getTaskTotalCost() {
		return taskTotalCost;
	}
	public void setTaskTotalCost(BigDecimal taskTotalCost) {
		this.taskTotalCost = taskTotalCost;
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
	public BigDecimal getTaskTotalCostRechN() {
		return taskTotalCostRechN;
	}
	public void setTaskTotalCostRechN(BigDecimal taskTotalCostRechN) {
		this.taskTotalCostRechN = taskTotalCostRechN;
	}
	public String getMaintReqServiceProviderDetail() {
		return maintReqServiceProviderDetail;
	}
	public void setMaintReqServiceProviderDetail(
			String maintReqServiceProviderDetail) {
		this.maintReqServiceProviderDetail = maintReqServiceProviderDetail;
	}
	public Date getMafsInvoiceDate() {
		return mafsInvoiceDate;
	}
	public void setMafsInvoiceDate(Date mafsInvoiceDate) {
		this.mafsInvoiceDate = mafsInvoiceDate;
	}
	public String getServiceProviderTelephone() {
		return serviceProviderTelephone;
	}
	public void setServiceProviderTelephone(String serviceProviderTelephone) {
		this.serviceProviderTelephone = serviceProviderTelephone;
	}
	//Added for Bug 16387
	public BigDecimal getActualInvoice() {
		return actualInvoice;
	}
	public void setActualInvoice(BigDecimal actualInvoice) {
		this.actualInvoice = actualInvoice;
	}
	public BigDecimal getActualCustCharge() {
		return actualCustCharge;
	}
	public void setActualCustCharge(BigDecimal actualCustCharge) {
		this.actualCustCharge = actualCustCharge;
	}
}
