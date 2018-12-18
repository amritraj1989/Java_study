package com.mikealbert.data.vo;

import java.io.Serializable;

public class VendorInvoiceDetailsVO implements Serializable {

	private static final long serialVersionUID = -8715923994927048069L;
	
	private String messageId;
	private String parentProvider;
	private String recordId;
	private Long lineId;
	
	private String recordType;
	private String partServiceCode;  
	private String partServiceDesc; 
	private String qty;
	private String unitCost;
	private String taxAmount;
	private String exciseTax;
	private String discRebateAmt;
	private String totalCost;

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
	public Long getLineId() {
		return lineId;
	}
	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}
	public String getPartServiceCode() {
		return partServiceCode;
	}
	public void setPartServiceCode(String partServiceCode) {
		this.partServiceCode = partServiceCode;
	}
	public String getPartServiceDesc() {
		return partServiceDesc;
	}
	public void setPartServiceDesc(String partServiceDesc) {
		this.partServiceDesc = partServiceDesc;
	}	
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(String unitCost) {
		this.unitCost = unitCost;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	public String getExciseTax() {
		return exciseTax;
	}
	public void setExciseTax(String exciseTax) {
		this.exciseTax = exciseTax;
	}
	public String getDiscRebateAmt() {
		return discRebateAmt;
	}
	public void setDiscRebateAmt(String discRebateAmt) {
		this.discRebateAmt = discRebateAmt;
	}
	public String getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}

}
