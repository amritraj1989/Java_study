package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
* The persistent class for the DEBIT_CREDIT_TRANSACTIONS database table.
*/

@Entity
@Table(name = "DEBIT_CREDIT_TRANSACTIONS")
public class DebitCreditMemoTransaction extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;   
    
    @Id
    @NotNull    
    @Column(name = "DC_NO")
    private Long dcNo;   
    
    @NotNull
    @Column(name = "DCMT_DCMT_ID")
    private Long dcmtDcmtId;
    
    @Column(name = "TAX_ONLY")
    private String taxOnly;
    
    @Column(name = "TICKET_NO")
    private String ticketNo;   
    
    @NotNull
    @Column(name = "REASON")
    private String reason;   
   
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;
    
    @NotNull
    @Column(name = "UNIT_NO")
    private String unitNo;        
    
    @Column(name = "IS_CLIENT_UNIT")
    private String isClientUnit;   
    
    @NotNull    
    @Column(name = "CATEGORY_ID")
    private Long categoryId;  
    
    @NotNull    
    @Column(name = "ANALYSIS_CODE")
    private String analysisCode;     
    
    @Column(name = "NET_AMOUNT")
    private BigDecimal netAmount;       
    
    @Column(name = "TAX_AMOUNT")
    private BigDecimal taxAmount;    
    
    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;    

	@Column(name = "TRANSACTION_DATE")
    private Date transactionDate;     
    
    @Column(name = "INVOICE_NO")
    private String invoiceNo;       
    
    @Column(name = "RENT_APPLICABLE_DATE")
    private Date rentApplicableDate;    
    
    @Column(name = "INVOICE_NOTE")
    private String invoiceNote;  
    
    @Column(name = "TP_SEQ_NO_AR")
    private Long tpSeqNoAr;
   
	@Column(name = "SELECTED_APPROVER")
    private String selectedApprover;      
    
    @NotNull   
    @Column(name = "STATUS")
    private String status; 
    
    @Column(name = "LINE_DESCRIPTION")
    private String lineDescription;     
    
    @Column(name = "GL_CODE")
    private String glCode;        
    
    @Column(name = "SUBMITTER")
    private String submitter;     
    
    @Column(name = "SUBMITTED_DATE")
    private Date submittedDate;      
    
    @Column(name = "APPROVER")
    private String approver;    
    
    @Column(name = "APPROVED_DATE")
    private Date approvedDate;    
    
    @Column(name = "PROCESSOR")
    private String processor;    
    
    @Column(name = "PROCESSED_DATE")
    private Date processedDate;    
    
    @Column(name = "ALLOCATOR")
    private String allocator;        
    
    @Column(name = "ALLOCATED_DATE")
    private Date allocatedDate;   
    
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;        
    
    @Column(name = "LAST_MODIFIED_DATE")
    private Date lastModifiedDate;    

    @Column(name = "SOURCE")
    private String source;        

    @Column(name = "WARNING_IND")
    private String warningInd;        
    
    public DebitCreditMemoTransaction() {}

	public Long getDcNo() {
		return dcNo;
	}

	public void setDcNo(Long dcNo) {
		this.dcNo = dcNo;
	}

	public Long getDcmtDcmtId() {
		return dcmtDcmtId;
	}

	public void setDcmtDcmtId(Long dcmtDcmtId) {
		this.dcmtDcmtId = dcmtDcmtId;
	}

	public String getTaxOnly() {
		return taxOnly;
	}

	public void setTaxOnly(String taxOnly) {
		this.taxOnly = taxOnly;
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

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
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

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}	

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getRentApplicableDate() {
		return rentApplicableDate;
	}

	public void setRentApplicableDate(Date rentApplicableDate) {
		this.rentApplicableDate = rentApplicableDate;
	}

	public String getInvoiceNote() {
		return invoiceNote;
	}

	public void setInvoiceNote(String invoiceNote) {
		this.invoiceNote = invoiceNote;
	}

	 public Long getTpSeqNoAr() {
			return tpSeqNoAr;
	}

	public void setTpSeqNoAr(Long tpSeqNoAr) {
		this.tpSeqNoAr = tpSeqNoAr;
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

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
    public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public String getWarningInd() {
		return warningInd;
	}

	public void setWarningInd(String warningInd) {
		this.warningInd = warningInd;
	}

}


