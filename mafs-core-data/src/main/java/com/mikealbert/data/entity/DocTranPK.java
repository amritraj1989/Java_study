package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DOC_TRANS database table.
 * 
 */
@Embeddable
public class DocTranPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID")
	private long cId;

	@Column(name="DOC_TYPE")
	private String docType;

	@Column(name="TRAN_TYPE")
	private String tranType;

	public DocTranPK() {
	}
	public long getCId() {
		return this.cId;
	}
	public void setCId(long cId) {
		this.cId = cId;
	}
	public String getDocType() {
		return this.docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getTranType() {
		return this.tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DocTranPK)) {
			return false;
		}
		DocTranPK castOther = (DocTranPK)other;
		return 
			(this.cId == castOther.cId)
			&& this.docType.equals(castOther.docType)
			&& this.tranType.equals(castOther.tranType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.cId ^ (this.cId >>> 32)));
		hash = hash * prime + this.docType.hashCode();
		hash = hash * prime + this.tranType.hashCode();
		
		return hash;
	}
}