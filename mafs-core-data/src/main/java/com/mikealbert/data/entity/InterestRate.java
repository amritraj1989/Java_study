package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the INTEREST_RATES database table.
 * 
 */
@Entity
@Table(name="INTEREST_RATES")
public class InterestRate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="IR_ID")
	private long irId;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM")
	private Date effectiveFrom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_TO")
	private Date effectiveTo;

	//bi-directional many-to-one association to InterestTypeCode
    @ManyToOne
	@JoinColumn(name="INTEREST_TYPE")
	private InterestTypeCode interestTypeCode;

	//bi-directional many-to-one association to InterestYearRate
	@OneToMany(mappedBy="interestRateBean")
	private List<InterestYearRate> interestYearRates;

    public InterestRate() {
    }

	public long getIrId() {
		return this.irId;
	}

	public void setIrId(long irId) {
		this.irId = irId;
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

	public InterestTypeCode getInterestTypeCode() {
		return this.interestTypeCode;
	}

	public void setInterestTypeCode(InterestTypeCode interestTypeCode) {
		this.interestTypeCode = interestTypeCode;
	}
	
	public List<InterestYearRate> getInterestYearRates() {
		return this.interestYearRates;
	}

	public void setInterestYearRates(List<InterestYearRate> interestYearRates) {
		this.interestYearRates = interestYearRates;
	}
	
}