package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the CAPITAL_ELEMENT_RULES database table.
 * 
 */
@Entity
@Table(name="CAPITAL_ELEMENT_RULES")
public class CapitalElementRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CER_ID")
	private Long cerId;

	@Column(name="FIXED_ASSET")
	private String fixedAsset;

	@Column(name="LEL_LEL_ID")
	private Long lelLelId;

	@Column(name="MAK_MAK_ID")
	private Long makMakId;

	@Column(name="ON_INVOICE")
	private String onInvoice;

	@Column(name="PURCHASE_ORDER")
	private String purchaseOrder;

	@Column(name="QUOTATION_CALCULATION")
	private String quotationCalculation;

	@Column(name="QUOTE_CAPITAL")
	private String quoteCapital;

	@Column(name="QUOTE_CONCEALED")
	private String quoteConcealed;

	private String rechargeable;

	private String reclaimable;

	@Column(name="SUP_ACCOUNT_CODE")
	private String supAccountCode;

	@Column(name="SUP_ACCOUNT_TYPE")
	private String supAccountType;

	@Column(name="SUP_C_ID")
	private Long supCId;

	//bi-directional many-to-one association to CapitalElement
	@ManyToOne
	@JoinColumn(name="CEL_CEL_ID")
	private CapitalElement capitalElement;

	//bi-directional many-to-one association to QuotationCapitalElement
	@OneToMany(mappedBy="capitalElementRule")
	private List<QuotationCapitalElement> quotationCapitalElements;

	public CapitalElementRule() {
	}

	public Long getCerId() {
		return this.cerId;
	}

	public void setCerId(Long cerId) {
		this.cerId = cerId;
	}

	public String getFixedAsset() {
		return this.fixedAsset;
	}

	public void setFixedAsset(String fixedAsset) {
		this.fixedAsset = fixedAsset;
	}

	public Long getLelLelId() {
		return this.lelLelId;
	}

	public void setLelLelId(Long lelLelId) {
		this.lelLelId = lelLelId;
	}

	public Long getMakMakId() {
		return this.makMakId;
	}

	public void setMakMakId(Long makMakId) {
		this.makMakId = makMakId;
	}

	public String getOnInvoice() {
		return this.onInvoice;
	}

	public void setOnInvoice(String onInvoice) {
		this.onInvoice = onInvoice;
	}

	public String getPurchaseOrder() {
		return this.purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public String getQuotationCalculation() {
		return this.quotationCalculation;
	}

	public void setQuotationCalculation(String quotationCalculation) {
		this.quotationCalculation = quotationCalculation;
	}

	public String getQuoteCapital() {
		return this.quoteCapital;
	}

	public void setQuoteCapital(String quoteCapital) {
		this.quoteCapital = quoteCapital;
	}

	public String getQuoteConcealed() {
		return this.quoteConcealed;
	}

	public void setQuoteConcealed(String quoteConcealed) {
		this.quoteConcealed = quoteConcealed;
	}

	public String getRechargeable() {
		return this.rechargeable;
	}

	public void setRechargeable(String rechargeable) {
		this.rechargeable = rechargeable;
	}

	public String getReclaimable() {
		return this.reclaimable;
	}

	public void setReclaimable(String reclaimable) {
		this.reclaimable = reclaimable;
	}

	public String getSupAccountCode() {
		return this.supAccountCode;
	}

	public void setSupAccountCode(String supAccountCode) {
		this.supAccountCode = supAccountCode;
	}

	public String getSupAccountType() {
		return this.supAccountType;
	}

	public void setSupAccountType(String supAccountType) {
		this.supAccountType = supAccountType;
	}

	public Long getSupCId() {
		return this.supCId;
	}

	public void setSupCId(Long supCId) {
		this.supCId = supCId;
	}

	public CapitalElement getCapitalElement() {
		return this.capitalElement;
	}

	public void setCapitalElement(CapitalElement capitalElement) {
		this.capitalElement = capitalElement;
	}

	public List<QuotationCapitalElement> getQuotationCapitalElements() {
		return this.quotationCapitalElements;
	}

	public void setQuotationCapitalElements(List<QuotationCapitalElement> quotationCapitalElements) {
		this.quotationCapitalElements = quotationCapitalElements;
	}
	

}