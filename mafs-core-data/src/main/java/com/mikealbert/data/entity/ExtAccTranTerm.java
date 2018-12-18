package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the EXT_ACC_TRAN_TERMS database table.
 * 
 */
@Entity
@Table(name="EXT_ACC_TRAN_TERMS")
public class ExtAccTranTerm implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ExtAccTranTermPK id;

	@Column(name="BILLING_FREQUENCY")
	private String billingFrequency;

	@Column(name="CR_C_ID")
	private java.math.BigDecimal crCId;

	@Column(name="CR_CODE")
	private String crCode;

	@Column(name="CR_EXT_ACC_TYPE")
	private String crExtAccType;

	@Column(name="IGNORE_INVOICE_TO_HIERARCHY")
	private String ignoreInvoiceToHierarchy;

	@Column(name="INVOICE_PRINT_REPORT")
	private String invoicePrintReport;

	@Column(name="PMC_CODE")
	private String pmcCode;

	

	public ExtAccTranTerm() {
	}

	public ExtAccTranTermPK getId() {
		return this.id;
	}

	public void setId(ExtAccTranTermPK id) {
		this.id = id;
	}

	public String getBillingFrequency() {
		return this.billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public java.math.BigDecimal getCrCId() {
		return this.crCId;
	}

	public void setCrCId(java.math.BigDecimal crCId) {
		this.crCId = crCId;
	}

	public String getCrCode() {
		return this.crCode;
	}

	public void setCrCode(String crCode) {
		this.crCode = crCode;
	}

	public String getCrExtAccType() {
		return this.crExtAccType;
	}

	public void setCrExtAccType(String crExtAccType) {
		this.crExtAccType = crExtAccType;
	}

	public String getIgnoreInvoiceToHierarchy() {
		return this.ignoreInvoiceToHierarchy;
	}

	public void setIgnoreInvoiceToHierarchy(String ignoreInvoiceToHierarchy) {
		this.ignoreInvoiceToHierarchy = ignoreInvoiceToHierarchy;
	}

	public String getInvoicePrintReport() {
		return this.invoicePrintReport;
	}

	public void setInvoicePrintReport(String invoicePrintReport) {
		this.invoicePrintReport = invoicePrintReport;
	}

	public String getPmcCode() {
		return this.pmcCode;
	}

	public void setPmcCode(String pmcCode) {
		this.pmcCode = pmcCode;
	}

	

	
}