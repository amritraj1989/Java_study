package com.mikealbert.data.vo;

import java.util.Date;
import java.util.List;

public class VehicleConfigVendorQuoteVO {
	
	@SuppressWarnings("unused")
	private Long rowKey;
	private Long vuqId;
	private Long ufqId;
	private boolean usedExitingQuote;
	private Long cId;
	private String vendorName;
	private String vendorCode;
	private String vendorType;
	private String quoteNumber;
	private String quoteDescription;
	private Date quoteDate;
	private Date quoteExpDate;
	private boolean readOnly;
	private List<DocumentFileVO> documents;
	private String status;
	
	public Long getRowKey() {
		return  Long.valueOf(this.hashCode());
	}
	public void setRowKey(Long rowKey) {
		this.rowKey = rowKey;
	}
	public Long getVuqId() {
		return vuqId;
	}
	public void setVuqId(Long vuqId) {
		this.vuqId = vuqId;
	}
	public Long getUfqId() {
		return ufqId;
	}
	public void setUfqId(Long ufqId) {
		this.ufqId = ufqId;
	}
	public List<DocumentFileVO> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentFileVO> documents) {
		this.documents = documents;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getVendorType() {
		return vendorType;
	}
	public void setVendorType(String vendorType) {
		this.vendorType = vendorType;
	}
	public Long getcId() {
		return cId;
	}
	public void setcId(Long cId) {
		this.cId = cId;
	}
	public String getQuoteNumber() {
		return quoteNumber;
	}
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	public String getQuoteDescription() {
		return quoteDescription;
	}
	public void setQuoteDescription(String quoteDescription) {
		this.quoteDescription = quoteDescription;
	}
	public Date getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	public Date getQuoteExpDate() {
		return quoteExpDate;
	}
	public void setQuoteExpDate(Date quoteExpDate) {
		this.quoteExpDate = quoteExpDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isUsedExitingQuote() {
		return usedExitingQuote;
	}
	public void setUsedExitingQuote(boolean usedExitingQuote) {
		this.usedExitingQuote = usedExitingQuote;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	
}
