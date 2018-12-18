package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to DELAY_REASONS table
 */

@Entity
@Table(name = "DELAY_REASONS")
public class DelayReason extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DR_CODE", updatable = false)
	private String delayReasonCode;
	
	@Column(name = "DR_DESCRIPTION", updatable = false)
    private String delayReasonDescription;
	
    public DelayReason(){
    }

	public String getDelayReasonCode() {
		return delayReasonCode;
	}

	public void setDelayReasonCode(String delayReasonCode) {
		this.delayReasonCode = delayReasonCode;
	}

	public String getDelayReasonDescription() {
		return delayReasonDescription;
	}

	public void setDelayReasonDescription(String delayReasonDescription) {
		this.delayReasonDescription = delayReasonDescription;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (delayReasonCode != null ? delayReasonCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DelayReason)) {
            return false;
        }
        DelayReason other = (DelayReason) object;
        if ((this.delayReasonCode == null && other.delayReasonCode != null) || (this.delayReasonCode != null && !this.delayReasonCode.equals(other.delayReasonCode))) {
            return false;
        }
        return true;
    }
}
