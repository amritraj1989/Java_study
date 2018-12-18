package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the CREDIT_TERMS database table.
 * 
 */
@Embeddable
public class CreditTermPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID")
	private long cId;

	@Column(name="EXT_ACC_TYPE")
	private String extAccType;

	@Column(name="CREDIT_TERMS_CODE")
	private String creditTermsCode;

	public CreditTermPK() {
	}
	public long getCId() {
		return this.cId;
	}
	public void setCId(long cId) {
		this.cId = cId;
	}
	public String getExtAccType() {
		return this.extAccType;
	}
	public void setExtAccType(String extAccType) {
		this.extAccType = extAccType;
	}
	public String getCreditTermsCode() {
		return this.creditTermsCode;
	}
	public void setCreditTermsCode(String creditTermsCode) {
		this.creditTermsCode = creditTermsCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CreditTermPK)) {
			return false;
		}
		CreditTermPK castOther = (CreditTermPK)other;
		return 
			(this.cId == castOther.cId)
			&& this.extAccType.equals(castOther.extAccType)
			&& this.creditTermsCode.equals(castOther.creditTermsCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.cId ^ (this.cId >>> 32)));
		hash = hash * prime + this.extAccType.hashCode();
		hash = hash * prime + this.creditTermsCode.hashCode();
		
		return hash;
	}
}