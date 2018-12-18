package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the CAPITAL_ELEMENTS database table.
 * @author Singh
 */
@Entity
@Table(name="CAPITAL_ELEMENTS")
public class CapitalElement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CEL_ID", unique=true, nullable=false, precision=12)
	private long celId;

	@Column(name="CAP_ELEMENT_TYPE", nullable=false, length=10)
	private String capElementType;

	@Column(name="CATEGORY_CODE", length=10)
	private String categoryCode;

	@Column(nullable=false, length=25)
	private String code;

	@Column(nullable=false, length=80)
	private String description;

	@Column(name="FIXED_ASSET", nullable=false, length=1)
	private String fixedAsset;

	@Column(name="FMV_INCLUDE", length=1)
	private String fmvInclude;

	@Column(name="LF_MARGIN_ONLY", nullable=false, length=1)
	private String lfMarginOnly;

	@Column(name="ON_INVOICE", nullable=false, length=1)
	private String onInvoice;

	@Column(name="PURCHASE_ORDER", nullable=false, length=1)
	private String purchaseOrder;

	@Column(name="QUOTATION_CALCULATION", nullable=false, length=80)
	private String quotationCalculation;

	@Column(name="QUOTE_CAPITAL", nullable=false, length=1)
	private String quoteCapital;

	@Column(name="QUOTE_CONCEALED", nullable=false, length=1)
	private String quoteConcealed;

	@Column(nullable=false, length=1)
	private String rechargeable;

	@Column(name="RECLAIM_AGAINST", length=1)
	private String reclaimAgainst;

	@Column(name="RECLAIM_CALCULATION", length=80)
	private String reclaimCalculation;

	@Column(nullable=false, length=1)
	private String reclaimable;

	//bi-directional many-to-one association to CapitalElementRule
	@OneToMany(mappedBy="capitalElement")
	private List<CapitalElementRule> capitalElementRules;

    public CapitalElement() {
    }

	public long getCelId() {
		return this.celId;
	}

	public void setCelId(long celId) {
		this.celId = celId;
	}

	public String getCapElementType() {
		return this.capElementType;
	}

	public void setCapElementType(String capElementType) {
		this.capElementType = capElementType;
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFixedAsset() {
		return this.fixedAsset;
	}

	public void setFixedAsset(String fixedAsset) {
		this.fixedAsset = fixedAsset;
	}

	public String getFmvInclude() {
		return this.fmvInclude;
	}

	public void setFmvInclude(String fmvInclude) {
		this.fmvInclude = fmvInclude;
	}

	public String getLfMarginOnly() {
		return this.lfMarginOnly;
	}

	public void setLfMarginOnly(String lfMarginOnly) {
		this.lfMarginOnly = lfMarginOnly;
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

	public String getReclaimAgainst() {
		return this.reclaimAgainst;
	}

	public void setReclaimAgainst(String reclaimAgainst) {
		this.reclaimAgainst = reclaimAgainst;
	}

	public String getReclaimCalculation() {
		return this.reclaimCalculation;
	}

	public void setReclaimCalculation(String reclaimCalculation) {
		this.reclaimCalculation = reclaimCalculation;
	}

	public String getReclaimable() {
		return this.reclaimable;
	}

	public void setReclaimable(String reclaimable) {
		this.reclaimable = reclaimable;
	}	
	
	public List<CapitalElementRule> getCapitalElementRules() {
		return this.capitalElementRules;
	}

	public void setCapitalElementRules(List<CapitalElementRule> capitalElementRules) {
		this.capitalElementRules = capitalElementRules;
	}
	
}