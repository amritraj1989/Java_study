package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;

public class AccessoryMaintJobActivationPK implements Serializable{

	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name="FMS_FMS_ID")
	private long fmsFmsId;
	
	@Basic(optional = false)
	@Column(name="NEW_QMD_ID")
	private long newQmdId;

	@Basic(optional = false)
	@Column(name="OLD_QMD_ID")
	private long oldQmdId;

	public long getFmsFmsId() {
		return fmsFmsId;
	}

	public void setFmsFmsId(long fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	public long getNewQmdId() {
		return newQmdId;
	}

	public void setNewQmdId(long newQmdId) {
		this.newQmdId = newQmdId;
	}

	public long getOldQmdId() {
		return oldQmdId;
	}

	public void setOldQmdId(long oldQmdId) {
		this.oldQmdId = oldQmdId;
	}
	
	
}
