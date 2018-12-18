package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TAX_CODES database table.
 * 
 */
@Entity
@Table(name="TAX_CODES")
public class TaxCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TC_ID")
	private Long tcId;

	@Temporal(TemporalType.DATE)
	@Column(name="EFFECTIVE_DATE")
	private Date effectiveDate;

	@Column(name="TAX_CODE")
	private String taxCode;

	@Column(name="TAX_RATE")
	private BigDecimal taxRate;

	public TaxCode() {
	}

	public Long getTcId() {
		return this.tcId;
	}

	public void setTcId(Long tcId) {
		this.tcId = tcId;
	}

	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
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