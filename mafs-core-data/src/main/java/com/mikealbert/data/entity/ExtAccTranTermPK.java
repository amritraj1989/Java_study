package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the EXT_ACC_TRAN_TERMS database table.
 * 
 */
@Embeddable
public class ExtAccTranTermPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="EA_C_ID")
	private long eaCId;

	@Column(name="EA_ACCOUNT_TYPE")
	private String eaAccountType;

	@Column(name="EA_ACCOUNT_CODE")
	private String eaAccountCode;

	//bi-directional many-to-one association to DocTran
		@ManyToOne
		@JoinColumns({
			@JoinColumn(name="DT_C_ID", referencedColumnName="C_ID"),
			@JoinColumn(name="DT_DOC_TYPE", referencedColumnName="DOC_TYPE"),
			@JoinColumn(name="DT_TRAN_TYPE", referencedColumnName="TRAN_TYPE")
			})
		private DocTran docTran;

	public ExtAccTranTermPK() {
	}
	public long getEaCId() {
		return this.eaCId;
	}
	public void setEaCId(long eaCId) {
		this.eaCId = eaCId;
	}
	public String getEaAccountType() {
		return this.eaAccountType;
	}
	public void setEaAccountType(String eaAccountType) {
		this.eaAccountType = eaAccountType;
	}
	public String getEaAccountCode() {
		return this.eaAccountCode;
	}
	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}
	

	public DocTran getDocTran() {
		return docTran;
	}
	public void setDocTran(DocTran docTran) {
		this.docTran = docTran;
	}
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ExtAccTranTermPK)) {
			return false;
		}
		ExtAccTranTermPK castOther = (ExtAccTranTermPK)other;
		return 
			(this.eaCId == castOther.eaCId)
			&& this.eaAccountType.equals(castOther.eaAccountType)
			&& this.eaAccountCode.equals(castOther.eaAccountCode)
			&& (this.docTran.getId().getCId()== castOther.docTran.getId().getCId())
			&& this.docTran.getId().getDocType().equals(castOther.docTran.getId().getDocType())
			&& this.docTran.getId().getTranType().equals(castOther.docTran.getId().getTranType());
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.eaCId ^ (this.eaCId >>> 32)));
		hash = hash * prime + this.eaAccountType.hashCode();
		hash = hash * prime + this.eaAccountCode.hashCode();
		hash = hash * prime + ((int) (this.docTran.getId().getCId() ^ (this.docTran.getId().getCId() >>> 32)));
		hash = hash * prime + this.docTran.getId().getDocType().hashCode();
		hash = hash * prime + this.docTran.getId().getTranType().hashCode();
		
		return hash;
	}
}