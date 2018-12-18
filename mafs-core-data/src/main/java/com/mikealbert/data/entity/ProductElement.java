package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PRODUCT_ELEMENTS")
public class ProductElement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4215511179889390080L;

	@Id
	@Column(name="PEL_ID", unique=true, nullable=false, precision=12)
	private long pelId;
	
    @JoinColumn(name = "LEL_LEL_ID", referencedColumnName = "LEL_ID")
    @ManyToOne(optional = false)
	private LeaseElement leaseElement;
    
	@Column(name="PRD_PRODUCT_CODE", nullable=false, length=10)
	private String prdProductCode;
	
	@Column(name="QUOTE_REPORT_GROUP", nullable=false, length=10)
	private String quoteReportGroup;
	
    @Column(name = "MANDATORY_IND", nullable=false, length=1)
    private String mandatoryInd;
    
    @Column(name = "ELEMENT_IND", nullable=false, length=1)
    private String elementInd;
	
	@Column(name="TAX_CODE", nullable=true)
	private BigDecimal taxCode;
	
	@Column(name="GROUP_CODE", nullable=true)
	private BigDecimal groupCode;

	public long getPelId() {
		return pelId;
	}

	public void setPelId(long pelId) {
		this.pelId = pelId;
	}

	public LeaseElement getLeaseElement() {
		return leaseElement;
	}

	public void setLeaseElement(LeaseElement leaseElement) {
		this.leaseElement = leaseElement;
	}

	public String getPrdProductCode() {
		return prdProductCode;
	}

	public void setPrdProductCode(String prdProductCode) {
		this.prdProductCode = prdProductCode;
	}

	public String getQuoteReportGroup() {
		return quoteReportGroup;
	}

	public void setQuoteReportGroup(String quoteReportGroup) {
		this.quoteReportGroup = quoteReportGroup;
	}

	public String getMandatoryInd() {
		return mandatoryInd;
	}

	public void setMandatoryInd(String mandatoryInd) {
		this.mandatoryInd = mandatoryInd;
	}

	public String getElementInd() {
		return elementInd;
	}

	public void setElementInd(String elementInd) {
		this.elementInd = elementInd;
	}

	public BigDecimal getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(BigDecimal taxCode) {
		this.taxCode = taxCode;
	}

	public BigDecimal getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(BigDecimal groupCode) {
		this.groupCode = groupCode;
	}
	
}
