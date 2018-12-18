package com.mikealbert.data.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the GL_CODE database table.
 * 
 */
@Embeddable
public class GlCodePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID")
	private long corpId;

	@Column(name="Code")
	private String code;

    public GlCodePK() {
    }
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GlCodePK)) {
			return false;
		}
		GlCodePK castOther = (GlCodePK)other;
		return 
			(this.corpId == castOther.corpId)
			&& this.code.equals(castOther.code);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.corpId ^ (this.corpId >>> 32)));
		hash = hash * prime + this.code.hashCode();
		
		return hash;
    }
	public long getCorpId() {
		return corpId;
	}
	public void setCorpId(long corpId) {
		this.corpId = corpId;
	}
}