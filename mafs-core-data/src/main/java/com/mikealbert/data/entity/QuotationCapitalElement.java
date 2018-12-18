package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTATION_CAPITAL_ELEMENTS database table.
 * @author Singh
 */
@Entity
@Table(name="QUOTATION_CAPITAL_ELEMENTS")
public class QuotationCapitalElement extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QCE_SEQ")
	@SequenceGenerator(name="QCE_SEQ", sequenceName="QCE_SEQ", allocationSize=1)
	@Column(name="QCE_ID", unique=true, nullable=false, precision=12)
	private long qceId;

	@Column(name="FIXED_ASSET", nullable=false, length=1)
	private String fixedAsset;

	@Column(name="ON_INVOICE", length=1)
	private String onInvoice;

	@Column(name="PURCHASE_ORDER", nullable=false, length=1)
	private String purchaseOrder;

	@Column(name="QUOTE_CAPITAL", nullable=false, length=1)
	private String quoteCapital;

	@Column(name="QUOTE_CONCEALED", nullable=false, length=1)
	private String quoteConcealed;

	@Column(length=1)
	private String rechargeable;

	@Column(name="RECLAIM_ACTIONED", nullable=false, length=1)
	private String reclaimActioned;

	@Column(length=1)
	private String reclaimable;

	@Column(name="\"VALUE\"", precision=30, scale=5)
	private BigDecimal value;

	//bi-directional many-to-one association to CapitalElement
    @ManyToOne
	@JoinColumn(name="CEL_CEL_ID", nullable=false)
	private CapitalElement capitalElement;

	@ManyToOne
	@JoinColumn(name="CER_CER_ID")
	private CapitalElementRule capitalElementRule;
	
	//bi-directional many-to-one association to CapitalEleSourceCode
    @ManyToOne
	@JoinColumn(name="SOURCE_CODE")
	private CapitalEleSourceCode capitalEleSourceCode;

	//bi-directional many-to-one association to QuotationModel
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="QMD_QMD_ID", nullable=false)
	private QuotationModel quotationModel;

    public QuotationCapitalElement() {
    }

	public long getQceId() {
		return this.qceId;
	}

	public void setQceId(long qceId) {
		this.qceId = qceId;
	}

	public String getFixedAsset() {
		return this.fixedAsset;
	}

	public void setFixedAsset(String fixedAsset) {
		this.fixedAsset = fixedAsset;
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

	public String getReclaimActioned() {
		return this.reclaimActioned;
	}

	public void setReclaimActioned(String reclaimActioned) {
		this.reclaimActioned = reclaimActioned;
	}

	public String getReclaimable() {
		return this.reclaimable;
	}

	public void setReclaimable(String reclaimable) {
		this.reclaimable = reclaimable;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public CapitalElement getCapitalElement() {
		return this.capitalElement;
	}

	public void setCapitalElement(CapitalElement capitalElement) {
		this.capitalElement = capitalElement;
	}
	
	public CapitalEleSourceCode getCapitalEleSourceCode() {
		return this.capitalEleSourceCode;
	}

	public void setCapitalEleSourceCode(CapitalEleSourceCode capitalEleSourceCode) {
		this.capitalEleSourceCode = capitalEleSourceCode;
	}
	
	public QuotationModel getQuotationModel() {
		return this.quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public CapitalElementRule getCapitalElementRule() {
		return capitalElementRule;
	}

	public void setCapitalElementRule(CapitalElementRule capitalElementRule) {
		this.capitalElementRule = capitalElementRule;
	}
	

	
}