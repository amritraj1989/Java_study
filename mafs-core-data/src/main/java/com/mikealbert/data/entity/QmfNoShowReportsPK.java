package com.mikealbert.data.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the GL_CODE database table.
 * 
 */
@Embeddable
public class QmfNoShowReportsPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QMF_QMF_ID")
	private long qmfQmfId;

	@Column(name="FPNSR_FPNSR_ID")
	private long fpnsrFpnsrId;

    public QmfNoShowReportsPK() {
    }

	public long getQmfQmfId() {
		return qmfQmfId;
	}

	public void setQmfQmfId(long qmfQmfId) {
		this.qmfQmfId = qmfQmfId;
	}

	public long getFpnsrFpnsrId() {
		return fpnsrFpnsrId;
	}

	public void setFpnsrFpnsrId(long fpnsrFpnsrId) {
		this.fpnsrFpnsrId = fpnsrFpnsrId;
	}
    
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QmfNoShowReportsPK)) {
			return false;
		}
		QmfNoShowReportsPK castOther = (QmfNoShowReportsPK)other;
		return 
			(this.qmfQmfId == castOther.qmfQmfId)
			&& this.fpnsrFpnsrId == castOther.fpnsrFpnsrId;

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.qmfQmfId ^ (this.qmfQmfId >>> 32)));
		hash = hash * prime + ((int) (this.fpnsrFpnsrId ^ (this.fpnsrFpnsrId >>> 32)));
		
		return hash;
    }
}