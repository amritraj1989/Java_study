package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the QUOTATION_PROFILE_FINANCES database table.
 * 
 */
@Embeddable
public class QuotationProfileFinancePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QPR_QPR_ID")
	private long qprQprId;

	@Column(name="PARAMETER_ID")
	private String parameterId;

    public QuotationProfileFinancePK() {
    }
	public long getQprQprId() {
		return this.qprQprId;
	}
	public void setQprQprId(long qprQprId) {
		this.qprQprId = qprQprId;
	}
	public String getParameterId() {
		return this.parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QuotationProfileFinancePK)) {
			return false;
		}
		QuotationProfileFinancePK castOther = (QuotationProfileFinancePK)other;
		return 
			(this.qprQprId == castOther.qprQprId)
			&& this.parameterId.equals(castOther.parameterId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.qprQprId ^ (this.qprQprId >>> 32)));
		hash = hash * prime + this.parameterId.hashCode();
		
		return hash;
    }
}