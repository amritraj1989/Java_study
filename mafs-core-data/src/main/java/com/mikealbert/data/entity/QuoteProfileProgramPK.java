package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the QUOTE_PROFILE_PROGRAMS database table.
 * 
 */
@Embeddable
public class QuoteProfileProgramPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QPR_QPR_ID", unique=true, nullable=false, precision=12)
	private long qprQprId;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM", unique=true, nullable=false)
	private java.util.Date effectiveFrom;

    public QuoteProfileProgramPK() {
    }
	public long getQprQprId() {
		return this.qprQprId;
	}
	public void setQprQprId(long qprQprId) {
		this.qprQprId = qprQprId;
	}
	public java.util.Date getEffectiveFrom() {
		return this.effectiveFrom;
	}
	public void setEffectiveFrom(java.util.Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QuoteProfileProgramPK)) {
			return false;
		}
		QuoteProfileProgramPK castOther = (QuoteProfileProgramPK)other;
		return 
			(this.qprQprId == castOther.qprQprId)
			&& this.effectiveFrom.equals(castOther.effectiveFrom);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.qprQprId ^ (this.qprQprId >>> 32)));
		hash = hash * prime + this.effectiveFrom.hashCode();
		
		return hash;
    }
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.QuoteProfileProgramPK[ qprQprId=" + qprQprId + " and effectiveFrom" + effectiveFrom+ " ]" ;
    }
}