package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the MUL_QUOTE database table.
 * 
 */
@Embeddable
public class MulQuotePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QUO_ID")
	private long quoId;

	@Column(name="GENERIC_ID")
	private long genericId;

	private String code;

	@Column(name="\"TYPE\"")
	private String type;

    public MulQuotePK() {
    }
	public long getQuoId() {
		return this.quoId;
	}
	public void setQuoId(long quoId) {
		this.quoId = quoId;
	}
	public long getGenericId() {
		return this.genericId;
	}
	public void setGenericId(long genericId) {
		this.genericId = genericId;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MulQuotePK)) {
			return false;
		}
		MulQuotePK castOther = (MulQuotePK)other;
		return 
			(this.quoId == castOther.quoId)
			&& (this.genericId == castOther.genericId)
			&& this.code.equals(castOther.code)
			&& this.type.equals(castOther.type);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.quoId ^ (this.quoId >>> 32)));
		hash = hash * prime + ((int) (this.genericId ^ (this.genericId >>> 32)));
		hash = hash * prime + this.code.hashCode();
		hash = hash * prime + this.type.hashCode();
		
		return hash;
    }
}