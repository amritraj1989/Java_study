package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the QUOTATION_STEP_STRUCTURE database table.
 * 
 */
@Embeddable
public class QuotationStepStructurePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QMD_QMD_ID", unique=true, nullable=false, precision=12)
	private long qmdQmdId;

	@Column(name="FROM_PERIOD", unique=true, nullable=false, precision=30, scale=5)
	private long fromPeriod;

    public QuotationStepStructurePK() {
    }
	public long getQmdQmdId() {
		return this.qmdQmdId;
	}
	public void setQmdQmdId(long qmdQmdId) {
		this.qmdQmdId = qmdQmdId;
	}
	public long getFromPeriod() {
		return this.fromPeriod;
	}
	public void setFromPeriod(long fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QuotationStepStructurePK)) {
			return false;
		}
		QuotationStepStructurePK castOther = (QuotationStepStructurePK)other;
		return 
			(this.qmdQmdId == castOther.qmdQmdId)
			&& (this.fromPeriod == castOther.fromPeriod);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.qmdQmdId ^ (this.qmdQmdId >>> 32)));
		hash = hash * prime + ((int) (this.fromPeriod ^ (this.fromPeriod >>> 32)));
		
		return hash;
    }
}