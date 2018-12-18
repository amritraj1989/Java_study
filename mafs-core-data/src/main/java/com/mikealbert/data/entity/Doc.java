package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


/**
 * The persistent class for the DOC database table.
 * @author Singh
 */


@Entity
@Table(name="DOC")
public class Doc extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_ID")
    @SequenceGenerator(name = "DOC_ID", sequenceName = "DOC_ID", allocationSize = 1)
	@Column(name="DOC_ID")
	private Long docId;

	@Column(name="ACCOUNT_CODE")
	private String accountCode;

	@Column(name="ACCOUNT_TYPE")
	private String accountType;

	@Column(name="AGREEMENT_ID")
	private Long agreementId;

	@Column(name="AUTHORISER_CODE")
	private String authoriserCode;

	@Column(name="BATCH_ID")
	private Long batchId;

    @Temporal( TemporalType.DATE)
	@Column(name="BILLED_FROM_DATE")
	private Date billedFromDate;

    @Temporal( TemporalType.DATE)
	@Column(name="BILLED_TO_DATE")
	private Date billedToDate;

	@Column(name="BRANCH_ACCOUNT_CODE")
	private String branchAccountCode;

	@Column(name="BRANCH_ACCOUNT_TYPE")
	private String branchAccountType;

	@Column(name="BRANCH_C_ID")
	private Long branchCId;

	@Column(name="C_ID")
	private Long cId;

	@Column(name="CB_AR_IND")
	private String cbArInd;

	@Column(name="CGH_CGH_ID")
	private Long cghCghId;

	@Column(name="COST_METHOD")
	private String costMethod;

	@Column(name="CRT_C_ID")
	private Long crtCId;

	@Column(name="CRT_EXT_ACC_TYPE")
	private String crtExtAccType;

	@Column(name="CURRENCY_CODE")
	private String currencyCode;

    @Temporal( TemporalType.DATE)
	@Column(name="DISC_DATE")
	private Date discDate;

    @Temporal( TemporalType.DATE)
	@Column(name="DOC_DATE")
	private Date docDate;

	@Column(name="DOC_DESCRIPTION")
	private String docDescription;

	@Column(name="DOC_DISC_PERCENT")
	private BigDecimal docDiscPercent;

	@Column(name="DOC_IND")
	private String docInd;

	@Column(name="DOC_NO")
	private String docNo;

	@Column(name="DOC_STATUS")
	private String docStatus;

	@Column(name="DOC_TYPE")
	private String docType;

	@Column(name="DRC_C_ID")
	private Long drcCId;

	@Column(name="DRC_DOC_TYPE")
	private String drcDocType;

	@Column(name="DRC_UPDATE_CONTROL_CODE")
	private String drcUpdateControlCode;

	@Column(name="DS_DOC_TYPE")
	private String dsDocType;

    @Temporal( TemporalType.DATE)
	@Column(name="DUE_DATE")
	private Date dueDate;

	@Column(name="DUE_DISC")
	private BigDecimal dueDisc;

	@Column(name="EA_C_ID")
	private Long eaCId;

    @Temporal( TemporalType.DATE)
	@Column(name="ENTERED_DATE")
	private Date enteredDate;

	@Column(name="EXCHANGE_RATE")
	private BigDecimal exchangeRate;

	@Column(name="EXCHANGE_RATE_TYPE")
	private String exchangeRateType;

	@Column(name="EXTERNAL_REF_NO")
	private String externalRefNo;

	@Column(name="EXTERNAL_REF_NOTE")
	private String externalRefNote;

	@Column(name="FOB_CODE")
	private String fobCode;

    @Temporal( TemporalType.DATE)
	@Column(name="FOREX_DATE")
	private Date forexDate;

	@Column(name="FOREX_REF")
	private String forexRef;

	@Column(name="GENERIC_EXT_ID")
	private Long genericExtId;

	@Column(name="GL_ACC")
	private BigDecimal glAcc;

	@Column(name="GL_STK")
	private BigDecimal glStk;

	@Column(name="HOLD_IND")
	private String holdInd;

	@Column(name="INS_APPROVED_IND")
	private String insApprovedInd;

	@Column(name="INTERNAL_REF_NO")
	private String internalRefNo;

	@Column(name="INVOICE_TO_ACCOUNT_CODE")
	private String invoiceToAccountCode;

	@Column(name="INVOICE_TO_ACCOUNT_TYPE")
	private String invoiceToAccountType;

	@Column(name="INVOICE_TO_C_ID")
	private Long invoiceToCId;

	@Column(name="JC_ID")
	private Long jcId;

	@Column(name="JCT_ID")
	private Long jctId;

	@Column(name="JNL_IND")
	private String jnlInd;

	@Column(name="JOB_NO")
	private String jobNo;

    @Temporal( TemporalType.DATE)
	@Column(name="LC_DATE")
	private Date lcDate;

	@Column(name="LC_REF")
	private String lcRef;

	@Column(name="OP_CODE")
	private String opCode;

	@Column(name="ORDER_TYPE")
	private String orderType;

	@Column(name="ORIG_CODE")
	private String origCode;

	@Column(name="ORIG_DISC")
	private BigDecimal origDisc;

	@Column(name="PAYMENT_AMOUNT")
	private BigDecimal paymentAmount;

	@Column(name="PAYMENT_BATCH_ID")
	private Long paymentBatchId;

	@Column(name="PAYMENT_METHOD")
	private String paymentMethod;

	@Column(name="PAYMENT_TERMS_CODE")
	private String paymentTermsCode;

	@Column(name="PERIODIC_PAYMENT_ID")
	private Long periodicPaymentId;

	private BigDecimal pod;

    @Temporal( TemporalType.DATE)
	@Column(name="POSTED_DATE")
	private Date postedDate;

	@Column(name="PRINTED_AMOUNT")
	private BigDecimal printedAmount;

	@Column(name="PRINTED_IND")
	private String printedInd;

	@Column(name="REASON_CODE")
	private String reasonCode;

    @Temporal( TemporalType.DATE)
	@Column(name="RECEIVED_DATE")
	private Date receivedDate;

	@Column(name="REFERENCE_CODE")
	private String referenceCode;

	@Column(name="REV_NO")
	private Long revNo;

	@Column(name="SOURCE_CODE")
	private String sourceCode;

	@Column(name="SUB_ACC_C_ID")
	private Long subAccCId;

	@Column(name="SUB_ACC_CODE")
	private String subAccCode;

	@Column(name="SUB_ACC_TYPE")
	private String subAccType;

	@Column(name="SUW_SUW_ID")
	private Long suwSuwId;

	@Column(name="TAX_CODE")
	private BigDecimal taxCode;

	@Column(name="TAX_EXEMPTION_NO")
	private String taxExemptionNo;

	@Column(name="TAX_IND")
	private String taxInd;

    @Temporal( TemporalType.DATE)
	@Column(name="TAX_POINT_DATE")
	private Date taxPointDate;

	@Column(name="TOTAL_DOC_COST")
	private BigDecimal totalDocCost;

	@Column(name="TOTAL_DOC_DISC")
	private BigDecimal totalDocDisc;

	@Column(name="TOTAL_DOC_FREIGHT")
	private BigDecimal totalDocFreight;

	@Column(name="TOTAL_DOC_INS")
	private BigDecimal totalDocIns;

	@Column(name="TOTAL_DOC_MISC")
	private BigDecimal totalDocMisc;

	@Column(name="TOTAL_DOC_PRICE")
	private BigDecimal totalDocPrice;

	@Column(name="TOTAL_DOC_TAX")
	private BigDecimal totalDocTax;

	@Column(name="TOTAL_DOC_WARR")
	private BigDecimal totalDocWarr;

	@Column(name="TOTAL_LAB_COST")
	private BigDecimal totalLabCost;

	@Column(name="TOTAL_LAB_PRICE")
	private BigDecimal totalLabPrice;

	@Column(name="TP_SEQ_NO")
	private BigDecimal tpSeqNo;

	@Column(name="TRANSFER_C_ID")
	private BigDecimal transferCId;

	@Column(name="UNALLOC_AMOUNT")
	private BigDecimal unallocAmount;

	@Column(name="UPDATE_CONTROL_CODE")
	private String updateControlCode;

	@Column(name="VEH_DESIGNATION_CODE")
	private String vehDesignationCode;

	//bi-directional many-to-one association to Dist
	@OneToMany(mappedBy="doc")
	private List<Dist> dists;

	//bi-directional many-to-one association to Docl
	@OneToMany(mappedBy="doc")
	private List<Docl> docls;
	

	//bi-directional many-to-one association to DocSupplier
	@OneToMany(mappedBy = "doc", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<DocSupplier> docSuppliers;

	@OneToMany(mappedBy = "doc", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<DocPropertyValue> docPropertyValues;
	
    public Doc() {}

	public long getDocId() {
		return this.docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getAccountCode() {
		return this.accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Long getAgreementId() {
		return this.agreementId;
	}

	public void setAgreementId(Long agreementId) {
		this.agreementId = agreementId;
	}

	public String getAuthoriserCode() {
		return this.authoriserCode;
	}

	public void setAuthoriserCode(String authoriserCode) {
		this.authoriserCode = authoriserCode;
	}

	public Long getBatchId() {
		return this.batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public Date getBilledFromDate() {
		return this.billedFromDate;
	}

	public void setBilledFromDate(Date billedFromDate) {
		this.billedFromDate = billedFromDate;
	}

	public Date getBilledToDate() {
		return this.billedToDate;
	}

	public void setBilledToDate(Date billedToDate) {
		this.billedToDate = billedToDate;
	}

	public String getBranchAccountCode() {
		return this.branchAccountCode;
	}

	public void setBranchAccountCode(String branchAccountCode) {
		this.branchAccountCode = branchAccountCode;
	}

	public String getBranchAccountType() {
		return this.branchAccountType;
	}

	public void setBranchAccountType(String branchAccountType) {
		this.branchAccountType = branchAccountType;
	}

	public Long getBranchCId() {
		return this.branchCId;
	}

	public void setBranchCId(Long branchCId) {
		this.branchCId = branchCId;
	}

	public Long getCId() {
		return this.cId;
	}

	public void setCId(Long cId) {
		this.cId = cId;
	}

	public String getCbArInd() {
		return this.cbArInd;
	}

	public void setCbArInd(String cbArInd) {
		this.cbArInd = cbArInd;
	}

	public Long getCghCghId() {
		return this.cghCghId;
	}

	public void setCghCghId(Long cghCghId) {
		this.cghCghId = cghCghId;
	}

	public String getCostMethod() {
		return this.costMethod;
	}

	public void setCostMethod(String costMethod) {
		this.costMethod = costMethod;
	}

	public Long getCrtCId() {
		return this.crtCId;
	}

	public void setCrtCId(Long crtCId) {
		this.crtCId = crtCId;
	}

	public String getCrtExtAccType() {
		return this.crtExtAccType;
	}

	public void setCrtExtAccType(String crtExtAccType) {
		this.crtExtAccType = crtExtAccType;
	}

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Date getDiscDate() {
		return this.discDate;
	}

	public void setDiscDate(Date discDate) {
		this.discDate = discDate;
	}

	public Date getDocDate() {
		return this.docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public String getDocDescription() {
		return this.docDescription;
	}

	public void setDocDescription(String docDescription) {
		this.docDescription = docDescription;
	}

	public BigDecimal getDocDiscPercent() {
		return this.docDiscPercent;
	}

	public void setDocDiscPercent(BigDecimal docDiscPercent) {
		this.docDiscPercent = docDiscPercent;
	}

	public String getDocInd() {
		return this.docInd;
	}

	public void setDocInd(String docInd) {
		this.docInd = docInd;
	}

	public String getDocNo() {
		return this.docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getDocStatus() {
		return this.docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getDocType() {
		return this.docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Long getDrcCId() {
		return this.drcCId;
	}

	public void setDrcCId(Long drcCId) {
		this.drcCId = drcCId;
	}

	public String getDrcDocType() {
		return this.drcDocType;
	}

	public void setDrcDocType(String drcDocType) {
		this.drcDocType = drcDocType;
	}

	public String getDrcUpdateControlCode() {
		return this.drcUpdateControlCode;
	}

	public void setDrcUpdateControlCode(String drcUpdateControlCode) {
		this.drcUpdateControlCode = drcUpdateControlCode;
	}

	public String getDsDocType() {
		return this.dsDocType;
	}

	public void setDsDocType(String dsDocType) {
		this.dsDocType = dsDocType;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getDueDisc() {
		return this.dueDisc;
	}

	public void setDueDisc(BigDecimal dueDisc) {
		this.dueDisc = dueDisc;
	}

	public Long getEaCId() {
		return this.eaCId;
	}

	public void setEaCId(Long eaCId) {
		this.eaCId = eaCId;
	}

	public Date getEnteredDate() {
		return this.enteredDate;
	}

	public void setEnteredDate(Date enteredDate) {
		this.enteredDate = enteredDate;
	}

	public BigDecimal getExchangeRate() {
		return this.exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getExchangeRateType() {
		return this.exchangeRateType;
	}

	public void setExchangeRateType(String exchangeRateType) {
		this.exchangeRateType = exchangeRateType;
	}

	public String getExternalRefNo() {
		return this.externalRefNo;
	}

	public void setExternalRefNo(String externalRefNo) {
		this.externalRefNo = externalRefNo;
	}

	public String getExternalRefNote() {
		return this.externalRefNote;
	}

	public void setExternalRefNote(String externalRefNote) {
		this.externalRefNote = externalRefNote;
	}

	public String getFobCode() {
		return this.fobCode;
	}

	public void setFobCode(String fobCode) {
		this.fobCode = fobCode;
	}

	public Date getForexDate() {
		return this.forexDate;
	}

	public void setForexDate(Date forexDate) {
		this.forexDate = forexDate;
	}

	public String getForexRef() {
		return this.forexRef;
	}

	public void setForexRef(String forexRef) {
		this.forexRef = forexRef;
	}

	public Long getGenericExtId() {
		return this.genericExtId;
	}

	public void setGenericExtId(Long genericExtId) {
		this.genericExtId = genericExtId;
	}

	public BigDecimal getGlAcc() {
		return this.glAcc;
	}

	public void setGlAcc(BigDecimal glAcc) {
		this.glAcc = glAcc;
	}

	public BigDecimal getGlStk() {
		return this.glStk;
	}

	public void setGlStk(BigDecimal glStk) {
		this.glStk = glStk;
	}

	public String getHoldInd() {
		return this.holdInd;
	}

	public void setHoldInd(String holdInd) {
		this.holdInd = holdInd;
	}

	public String getInsApprovedInd() {
		return this.insApprovedInd;
	}

	public void setInsApprovedInd(String insApprovedInd) {
		this.insApprovedInd = insApprovedInd;
	}

	public String getInternalRefNo() {
		return this.internalRefNo;
	}

	public void setInternalRefNo(String internalRefNo) {
		this.internalRefNo = internalRefNo;
	}

	public String getInvoiceToAccountCode() {
		return this.invoiceToAccountCode;
	}

	public void setInvoiceToAccountCode(String invoiceToAccountCode) {
		this.invoiceToAccountCode = invoiceToAccountCode;
	}

	public String getInvoiceToAccountType() {
		return this.invoiceToAccountType;
	}

	public void setInvoiceToAccountType(String invoiceToAccountType) {
		this.invoiceToAccountType = invoiceToAccountType;
	}

	public Long getInvoiceToCId() {
		return this.invoiceToCId;
	}

	public void setInvoiceToCId(Long invoiceToCId) {
		this.invoiceToCId = invoiceToCId;
	}

	public Long getJcId() {
		return this.jcId;
	}

	public void setJcId(Long jcId) {
		this.jcId = jcId;
	}

	public Long getJctId() {
		return this.jctId;
	}

	public void setJctId(Long jctId) {
		this.jctId = jctId;
	}

	public String getJnlInd() {
		return this.jnlInd;
	}

	public void setJnlInd(String jnlInd) {
		this.jnlInd = jnlInd;
	}

	public String getJobNo() {
		return this.jobNo;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

	public Date getLcDate() {
		return this.lcDate;
	}

	public void setLcDate(Date lcDate) {
		this.lcDate = lcDate;
	}

	public String getLcRef() {
		return this.lcRef;
	}

	public void setLcRef(String lcRef) {
		this.lcRef = lcRef;
	}

	public String getOpCode() {
		return this.opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrigCode() {
		return this.origCode;
	}

	public void setOrigCode(String origCode) {
		this.origCode = origCode;
	}

	public BigDecimal getOrigDisc() {
		return this.origDisc;
	}

	public void setOrigDisc(BigDecimal origDisc) {
		this.origDisc = origDisc;
	}

	public BigDecimal getPaymentAmount() {
		return this.paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Long getPaymentBatchId() {
		return this.paymentBatchId;
	}

	public void setPaymentBatchId(Long paymentBatchId) {
		this.paymentBatchId = paymentBatchId;
	}

	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentTermsCode() {
		return this.paymentTermsCode;
	}

	public void setPaymentTermsCode(String paymentTermsCode) {
		this.paymentTermsCode = paymentTermsCode;
	}

	public Long getPeriodicPaymentId() {
		return this.periodicPaymentId;
	}

	public void setPeriodicPaymentId(Long periodicPaymentId) {
		this.periodicPaymentId = periodicPaymentId;
	}

	public BigDecimal getPod() {
		return this.pod;
	}

	public void setPod(BigDecimal pod) {
		this.pod = pod;
	}

	public Date getPostedDate() {
		return this.postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public BigDecimal getPrintedAmount() {
		return this.printedAmount;
	}

	public void setPrintedAmount(BigDecimal printedAmount) {
		this.printedAmount = printedAmount;
	}

	public String getPrintedInd() {
		return this.printedInd;
	}

	public void setPrintedInd(String printedInd) {
		this.printedInd = printedInd;
	}

	public String getReasonCode() {
		return this.reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public Date getReceivedDate() {
		return this.receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getReferenceCode() {
		return this.referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public Long getRevNo() {
		return this.revNo;
	}

	public void setRevNo(Long revNo) {
		this.revNo = revNo;
	}

	public String getSourceCode() {
		return this.sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public Long getSubAccCId() {
		return this.subAccCId;
	}

	public void setSubAccCId(Long subAccCId) {
		this.subAccCId = subAccCId;
	}

	public String getSubAccCode() {
		return this.subAccCode;
	}

	public void setSubAccCode(String subAccCode) {
		this.subAccCode = subAccCode;
	}

	public String getSubAccType() {
		return this.subAccType;
	}

	public void setSubAccType(String subAccType) {
		this.subAccType = subAccType;
	}

	public Long getSuwSuwId() {
		return this.suwSuwId;
	}

	public void setSuwSuwId(Long suwSuwId) {
		this.suwSuwId = suwSuwId;
	}

	public BigDecimal getTaxCode() {
		return this.taxCode;
	}

	public void setTaxCode(BigDecimal taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxExemptionNo() {
		return this.taxExemptionNo;
	}

	public void setTaxExemptionNo(String taxExemptionNo) {
		this.taxExemptionNo = taxExemptionNo;
	}

	public String getTaxInd() {
		return this.taxInd;
	}

	public void setTaxInd(String taxInd) {
		this.taxInd = taxInd;
	}

	public Date getTaxPointDate() {
		return this.taxPointDate;
	}

	public void setTaxPointDate(Date taxPointDate) {
		this.taxPointDate = taxPointDate;
	}

	public BigDecimal getTotalDocCost() {
		return this.totalDocCost;
	}

	public void setTotalDocCost(BigDecimal totalDocCost) {
		this.totalDocCost = totalDocCost;
	}

	public BigDecimal getTotalDocDisc() {
		return this.totalDocDisc;
	}

	public void setTotalDocDisc(BigDecimal totalDocDisc) {
		this.totalDocDisc = totalDocDisc;
	}

	public BigDecimal getTotalDocFreight() {
		return this.totalDocFreight;
	}

	public void setTotalDocFreight(BigDecimal totalDocFreight) {
		this.totalDocFreight = totalDocFreight;
	}

	public BigDecimal getTotalDocIns() {
		return this.totalDocIns;
	}

	public void setTotalDocIns(BigDecimal totalDocIns) {
		this.totalDocIns = totalDocIns;
	}

	public BigDecimal getTotalDocMisc() {
		return this.totalDocMisc;
	}

	public void setTotalDocMisc(BigDecimal totalDocMisc) {
		this.totalDocMisc = totalDocMisc;
	}

	public BigDecimal getTotalDocPrice() {
		return this.totalDocPrice;
	}

	public void setTotalDocPrice(BigDecimal totalDocPrice) {
		this.totalDocPrice = totalDocPrice;
	}

	public BigDecimal getTotalDocTax() {
		return this.totalDocTax;
	}

	public void setTotalDocTax(BigDecimal totalDocTax) {
		this.totalDocTax = totalDocTax;
	}

	public BigDecimal getTotalDocWarr() {
		return this.totalDocWarr;
	}

	public void setTotalDocWarr(BigDecimal totalDocWarr) {
		this.totalDocWarr = totalDocWarr;
	}

	public BigDecimal getTotalLabCost() {
		return this.totalLabCost;
	}

	public void setTotalLabCost(BigDecimal totalLabCost) {
		this.totalLabCost = totalLabCost;
	}

	public BigDecimal getTotalLabPrice() {
		return this.totalLabPrice;
	}

	public void setTotalLabPrice(BigDecimal totalLabPrice) {
		this.totalLabPrice = totalLabPrice;
	}

	public BigDecimal getTpSeqNo() {
		return this.tpSeqNo;
	}

	public void setTpSeqNo(BigDecimal tpSeqNo) {
		this.tpSeqNo = tpSeqNo;
	}

	public BigDecimal getTransferCId() {
		return this.transferCId;
	}

	public void setTransferCId(BigDecimal transferCId) {
		this.transferCId = transferCId;
	}

	public BigDecimal getUnallocAmount() {
		return this.unallocAmount;
	}

	public void setUnallocAmount(BigDecimal unallocAmount) {
		this.unallocAmount = unallocAmount;
	}

	public String getUpdateControlCode() {
		return this.updateControlCode;
	}

	public void setUpdateControlCode(String updateControlCode) {
		this.updateControlCode = updateControlCode;
	}

	public String getVehDesignationCode() {
		return this.vehDesignationCode;
	}

	public void setVehDesignationCode(String vehDesignationCode) {
		this.vehDesignationCode = vehDesignationCode;
	}

	public List<Dist> getDists() {
		return dists;
	}

	public void setDists(List<Dist> dists) {
		this.dists = dists;
	}

	public List<Docl> getDocls() {
		return this.docls;
	}

	public void setDocls(List<Docl> docls) {
		this.docls = docls;
	}	
	
	public List<DocSupplier> getDocSuppliers() {
		return docSuppliers;
	}

	public void setDocSuppliers(List<DocSupplier> docSuppliers) {
		this.docSuppliers = docSuppliers;
	}

	@Transient	
	public DocSupplier getDocSupplier(String workshopCapability) {
		DocSupplier docSup = null;
		for(DocSupplier docSupplier : getDocSuppliers()) {		
			if(docSupplier.getWorkshopCapabilityCode().equals(workshopCapability)) {			
				docSup = docSupplier;
				break;
			}
		}
		return docSup;
	}	

	public List<DocPropertyValue> getDocPropertyValues() {
		return docPropertyValues;
	}

	public void setDocPropertyValues(List<DocPropertyValue> docPropertyValues) {
		this.docPropertyValues = docPropertyValues;
	}

	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.DOC[ docId=" + docId + " ]";
    }
	
	
}