package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DOC_TRAN_GL_CODES database table.
 * @author Roshan K
 */
@Embeddable
public class DocTranGlCodePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID")
	private long cId;

	@Column(name="DOC_TYPE")
	private String docType;

	@Column(name="TRAN_TYPE")
	private String tranType;

	@Column(name="GL_POSTING_TYPE")
	private String glPostingType;

	public DocTranGlCodePK() {
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
	public String getGlPostingType() {
		return this.glPostingType;
	}
	public void setGlPostingType(String glPostingType) {
		this.glPostingType = glPostingType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DocTranGlCodePK)) {
			return false;
		}
		DocTranGlCodePK castOther = (DocTranGlCodePK)other;
		return 
			(this.cId == castOther.cId)
			&& this.docType.equals(castOther.docType)
			&& this.tranType.equals(castOther.tranType)
			&& this.glPostingType.equals(castOther.glPostingType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.cId ^ (this.cId >>> 32)));
		hash = hash * prime + this.docType.hashCode();
		hash = hash * prime + this.tranType.hashCode();
		hash = hash * prime + this.glPostingType.hashCode();
		
		return hash;
	}
}