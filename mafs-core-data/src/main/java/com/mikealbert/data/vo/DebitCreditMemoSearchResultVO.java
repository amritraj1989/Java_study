package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;

public class DebitCreditMemoSearchResultVO {

	private Long dcNumber;
	private String clientCode;
	private String clientName;
	private String unitNo;
	private String statusCode;
	private String status;
	private String type;
	private String categoryCode;
	private String categoryDesc;
	private String analysisCode;
	private String analysisDesc;
	private String selectedApprover;
	private String submitter;
	private Date submittedDate;
	private String approver;
	private Date approvedDate;
	private String processor;
	private Date processedDate;
	private String allocator;
	private Date allocatedDate;
	private String invoiceNo;
	private BigDecimal netAmount;
	private BigDecimal totalAmount;
	private String lineDescription;
	private String driverAddressState;
	private String reason;
	private String source;
	private String ticketNo;
	private boolean warningInd;
	private Character isClientUnit;
	private BigDecimal taxAmount;
	private Date transactionDate;
	private Date rentApplicableDate;
	private String glCode;
	private String invoiceNote;
		
	public Long getDcNumber() {
		return dcNumber;
	}

	public void setDcNumber(Long dcNumber) {
		this.dcNumber = dcNumber;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public String getAnalysisDesc() {
		return analysisDesc;
	}

	public void setAnalysisDesc(String analysisDesc) {
		this.analysisDesc = analysisDesc;
	}

	public String getSelectedApprover() {
		return selectedApprover;
	}

	public void setSelectedApprover(String selectedApprover) {
		this.selectedApprover = selectedApprover;
	}
	
	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

	public String getAllocator() {
		return allocator;
	}

	public void setAllocator(String allocator) {
		this.allocator = allocator;
	}

	public Date getAllocatedDate() {
		return allocatedDate;
	}

	public void setAllocatedDate(Date allocatedDate) {
		this.allocatedDate = allocatedDate;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getLineDescription() {
		return lineDescription;
	}

	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}

	public String getDriverAddressState() {
		return driverAddressState;
	}

	public void setDriverAddressState(String driverAddressState) {
		this.driverAddressState = driverAddressState;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "DebitCreditMemoSearchResultVO [dcNumber=" + dcNumber + ", unitNo=" + unitNo + "]";
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public boolean isWarningInd() {
		return warningInd;
	}

	public void setWarningInd(boolean warningInd) {
		this.warningInd = warningInd;
	}

	public Character isClientUnit() {
		return isClientUnit;
	}

	public void setClientUnit(Character string) {
		this.isClientUnit = string;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getRentApplicableDate() {
		return rentApplicableDate;
	}

	public void setRentApplicableDate(Date rentApplicableDate) {
		this.rentApplicableDate = rentApplicableDate;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getInvoiceNote() {
		return invoiceNote;
	}

	public void setInvoiceNote(String invoiceNote) {
		this.invoiceNote = invoiceNote;
	}

}
