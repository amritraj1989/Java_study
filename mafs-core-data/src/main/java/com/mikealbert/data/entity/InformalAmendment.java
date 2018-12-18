package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the INFORMAL_AMENDMENTS database table.
 * 
 */
@Entity
@Table(name="INFORMAL_AMENDMENTS")
public class InformalAmendment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="IAD_ID")
	private long iadId;

	@Column(name="ADD_REMOVE")
	private String addRemove;

	@Column(name="BILLING_AMT")
	private BigDecimal billingAmt;

	@Column(name="CAPITAL_COST")
	private BigDecimal capitalCost;

	@Temporal(TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM")
	private Date effectiveFrom;

	@Column(name="LEL_LEL_ID")
	private Long lelLelId;

	@Column(name="OVERHEAD_AMT")
	private BigDecimal overheadAmt;

	@Column(name="PROFIT_AMT")
	private BigDecimal profitAmt;

	@Column(name="QMD_QMD_ID")
	private Long qmdQmdId;

	private BigDecimal rental;

	@Column(name="TAX_CODE")
	private String taxCode;

	@Column(name="TAX_RATE")
	private BigDecimal taxRate;

	public InformalAmendment() {
	}

	public long getIadId() {
		return this.iadId;
	}

	public void setIadId(long iadId) {
		this.iadId = iadId;
	}

	public String getAddRemove() {
		return this.addRemove;
	}

	public void setAddRemove(String addRemove) {
		this.addRemove = addRemove;
	}

	public BigDecimal getBillingAmt() {
		return this.billingAmt;
	}

	public void setBillingAmt(BigDecimal billingAmt) {
		this.billingAmt = billingAmt;
	}

	public BigDecimal getCapitalCost() {
		return this.capitalCost;
	}

	public void setCapitalCost(BigDecimal capitalCost) {
		this.capitalCost = capitalCost;
	}

	public Date getEffectiveFrom() {
		return this.effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Long getLelLelId() {
		return this.lelLelId;
	}

	public void setLelLelId(Long lelLelId) {
		this.lelLelId = lelLelId;
	}

	public BigDecimal getOverheadAmt() {
		return this.overheadAmt;
	}

	public void setOverheadAmt(BigDecimal overheadAmt) {
		this.overheadAmt = overheadAmt;
	}

	public BigDecimal getProfitAmt() {
		return this.profitAmt;
	}

	public void setProfitAmt(BigDecimal profitAmt) {
		this.profitAmt = profitAmt;
	}

	public Long getQmdQmdId() {
		return this.qmdQmdId;
	}

	public void setQmdQmdId(Long qmdQmdId) {
		this.qmdQmdId = qmdQmdId;
	}

	public BigDecimal getRental() {
		return this.rental;
	}

	public void setRental(BigDecimal rental) {
		this.rental = rental;
	}

	public String getTaxCode() {
		return this.taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public BigDecimal getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

}