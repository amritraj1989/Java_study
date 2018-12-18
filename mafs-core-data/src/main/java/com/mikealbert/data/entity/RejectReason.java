package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to REJECT_REASON_CODES table
 */

@Entity
@Table(name = "REJECT_REASON_CODES")
public class RejectReason extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "REJECT_REASON", updatable = false)
	private String rejectReasonCode;
	
	@Column(name = "DESCRIPTION", updatable = false)
    private String rejectReasonDescription;
	
    public String getRejectReasonCode() {
		return rejectReasonCode;
	}

	public void setRejectReasonCode(String rejectReasonCode) {
		this.rejectReasonCode = rejectReasonCode;
	}

	public String getRejectReasonDescription() {
		return rejectReasonDescription;
	}

	public void setRejectReasonDescription(String rejectReasonDescription) {
		this.rejectReasonDescription = rejectReasonDescription;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (rejectReasonCode != null ? rejectReasonCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RejectReason)) {
            return false;
        }
        RejectReason other = (RejectReason) object;
        if ((this.rejectReasonCode == null && other.rejectReasonCode != null) || (this.rejectReasonCode != null && !this.rejectReasonCode.equals(other.rejectReasonCode))) {
            return false;
        }
        return true;
    }
}
