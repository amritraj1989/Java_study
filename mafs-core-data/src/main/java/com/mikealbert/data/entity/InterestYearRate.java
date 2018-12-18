package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the INTEREST_YEAR_RATES database table.
 * 
 */
@Entity
@Table(name="INTEREST_YEAR_RATES")
public class InterestYearRate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="IYR_ID")
	private long iyrId;

	@Column(name="INTEREST_RATE")
	private BigDecimal interestRate;

	@Column(name="INTEREST_YEAR")
	private String interestYear;

	//bi-directional many-to-one association to InterestRate
    @ManyToOne
	@JoinColumn(name="IR_IR_ID")
	private InterestRate interestRateBean;

    public InterestYearRate() {
    }

	public long getIyrId() {
		return this.iyrId;
	}

	public void setIyrId(long iyrId) {
		this.iyrId = iyrId;
	}

	public BigDecimal getInterestRate() {
		return this.interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public String getInterestYear() {
		return this.interestYear;
	}

	public void setInterestYear(String interestYear) {
		this.interestYear = interestYear;
	}

	public InterestRate getInterestRateBean() {
		return this.interestRateBean;
	}

	public void setInterestRateBean(InterestRate interestRateBean) {
		this.interestRateBean = interestRateBean;
	}
	
}