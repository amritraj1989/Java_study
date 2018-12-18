package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the QUOTATION_PROFILE_PAYMENTS database table.
 * 
 */
@Embeddable
public class QuotationProfilePaymentPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PM_PAYMENT_ID")
	private long pmPaymentId;

	@Column(name="QPR_QPR_ID")
	private long qprQprId;

    public QuotationProfilePaymentPK() {
    }
	public long getPmPaymentId() {
		return this.pmPaymentId;
	}
	public void setPmPaymentId(long pmPaymentId) {
		this.pmPaymentId = pmPaymentId;
	}
	public long getQprQprId() {
		return this.qprQprId;
	}
	public void setQprQprId(long qprQprId) {
		this.qprQprId = qprQprId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QuotationProfilePaymentPK)) {
			return false;
		}
		QuotationProfilePaymentPK castOther = (QuotationProfilePaymentPK)other;
		return 
			(this.pmPaymentId == castOther.pmPaymentId)
			&& (this.qprQprId == castOther.qprQprId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.pmPaymentId ^ (this.pmPaymentId >>> 32)));
		hash = hash * prime + ((int) (this.qprQprId ^ (this.qprQprId >>> 32)));
		
		return hash;
    }
}