package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the PRODUCTS database table.
 * 
 */
@Entity
@Table(name="PRODUCTS")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PRODUCT_CODE")
	private String productCode;

	@Column(name="ASSET_TYPE")
	private String assetType;

	@Column(name="COUNT_AS_ON_ORDER")
	private String countAsOnOrder;

	private String description;

	@Column(name="DT_C_ID")
	private java.math.BigDecimal dtCId;

	@Column(name="DT_DOC_TYPE")
	private String dtDocType;

	@Column(name="DT_TRAN_TYPE")
	private String dtTranType;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM")
	private Date effectiveFrom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_TO")
	private Date effectiveTo;

	@Column(name="FOR_FOR_ID")
	private java.math.BigDecimal forForId;

	@Column(name="ICA_C_ID")
	private java.math.BigDecimal icaCId;

	@Column(name="PRODUCT_NAME")
	private String productName;

	@Column(name="PRODUCT_TYPE")
	private String productType;

	@Column(name="QUOTE_PERMITTED_IND")
	private String quotePermittedInd;

	@Column(name="RESIDUAL_VALUE_IND")
	private String residualValueInd;

	@Column(name="SUMMARY_VQ")
	private String summaryVq;

	//bi-directional many-to-one association to FinanceParameter
    @ManyToOne
	@JoinColumn(name="PRODUCT_OVERHEAD")
	private FinanceParameter financeParameter1;

	//bi-directional many-to-one association to FinanceParameter
    @ManyToOne
	@JoinColumn(name="PRODUCT_PROFIT")
	private FinanceParameter financeParameter2;

	//bi-directional many-to-one association to InterestTypeCode
    @ManyToOne
	@JoinColumn(name="MARGIN_INTEREST_TYPE")
	private InterestTypeCode interestTypeCode;
    
    @Column(name="CLIENT_PRODUCT_LIST_IND")
    private String clientProductListInd;

    public Product() {
    }

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getAssetType() {
		return this.assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getCountAsOnOrder() {
		return this.countAsOnOrder;
	}

	public void setCountAsOnOrder(String countAsOnOrder) {
		this.countAsOnOrder = countAsOnOrder;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.math.BigDecimal getDtCId() {
		return this.dtCId;
	}

	public void setDtCId(java.math.BigDecimal dtCId) {
		this.dtCId = dtCId;
	}

	public String getDtDocType() {
		return this.dtDocType;
	}

	public void setDtDocType(String dtDocType) {
		this.dtDocType = dtDocType;
	}

	public String getDtTranType() {
		return this.dtTranType;
	}

	public void setDtTranType(String dtTranType) {
		this.dtTranType = dtTranType;
	}

	public Date getEffectiveFrom() {
		return this.effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return this.effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public java.math.BigDecimal getForForId() {
		return this.forForId;
	}

	public void setForForId(java.math.BigDecimal forForId) {
		this.forForId = forForId;
	}

	public java.math.BigDecimal getIcaCId() {
		return this.icaCId;
	}

	public void setIcaCId(java.math.BigDecimal icaCId) {
		this.icaCId = icaCId;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return this.productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getQuotePermittedInd() {
		return this.quotePermittedInd;
	}

	public void setQuotePermittedInd(String quotePermittedInd) {
		this.quotePermittedInd = quotePermittedInd;
	}

	public String getResidualValueInd() {
		return this.residualValueInd;
	}

	public void setResidualValueInd(String residualValueInd) {
		this.residualValueInd = residualValueInd;
	}

	public String getSummaryVq() {
		return this.summaryVq;
	}

	public void setSummaryVq(String summaryVq) {
		this.summaryVq = summaryVq;
	}

	public FinanceParameter getFinanceParameter1() {
		return this.financeParameter1;
	}

	public void setFinanceParameter1(FinanceParameter financeParameter1) {
		this.financeParameter1 = financeParameter1;
	}
	
	public FinanceParameter getFinanceParameter2() {
		return this.financeParameter2;
	}

	public void setFinanceParameter2(FinanceParameter financeParameter2) {
		this.financeParameter2 = financeParameter2;
	}
	
	public InterestTypeCode getInterestTypeCode() {
		return this.interestTypeCode;
	}

	public void setInterestTypeCode(InterestTypeCode interestTypeCode) {
		this.interestTypeCode = interestTypeCode;
	}

	public String getClientProductListInd() {
		return this.clientProductListInd;
	}

	public void setClientProductListInd(String clientProductListInd) {
		this.clientProductListInd = clientProductListInd;
	}
	
}