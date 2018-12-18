package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.List;

public class DebitCreditTransactionVO implements Serializable {

	private static final long serialVersionUID = 3766927351464612348L;

	private List<String> errors;
	private String jmsMessageId;
	private String fileName;
    private String debitCreditType;
    private String ticketNo;   
    private String reason;   
	private String accountCode;	
    private String unitNo;        
    private String isClientUnit;   
    private String category;
    private String analysisCode;     
    private String netAmount;       
    private String taxAmount;    
    private String transactionDate;     
    private String invoiceNo;       
    private String rentApplicableDate;    
    private String invoiceNote;  
    private String selectedApprover;      
    private String status; 
    private String lineDescription;     
    private String glCode;        
    private String submitter;     
    private String submittedDate;      
    
	public String getDebitCreditType() {
		return debitCreditType;
	}
	public void setDebitCreditType(String debitCreditType) {
		this.debitCreditType = debitCreditType;
	}
	public String getTicketNo() {
		return ticketNo;
	}
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getIsClientUnit() {
		return isClientUnit;
	}
	public void setIsClientUnit(String isClientUnit) {
		this.isClientUnit = isClientUnit;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAnalysisCode() {
		return analysisCode;
	}
	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getRentApplicableDate() {
		return rentApplicableDate;
	}
	public void setRentApplicableDate(String rentApplicableDate) {
		this.rentApplicableDate = rentApplicableDate;
	}
	public String getInvoiceNote() {
		return invoiceNote;
	}
	public void setInvoiceNote(String invoiceNote) {
		this.invoiceNote = invoiceNote;
	}
	public String getSelectedApprover() {
		return selectedApprover;
	}
	public void setSelectedApprover(String selectedApprover) {
		this.selectedApprover = selectedApprover;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLineDescription() {
		return lineDescription;
	}
	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}
	public String getGlCode() {
		return glCode;
	}
	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public String getSubmittedDate() {
		return submittedDate;
	}
	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}
	public String getJmsMessageId() {
		return jmsMessageId;
	}
	public void setJmsMessageId(String jmsMessageId) {
		this.jmsMessageId = jmsMessageId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
