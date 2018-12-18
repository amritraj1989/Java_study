package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to RejectReason.java table
 */

@Entity
@Table(name = "QUOTE_REJECT_CODES")
public class QuoteRejectCode implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "REJECT_CODE", updatable = false)
	private String rejectCode;
	
	@Column(name = "DESCRIPTION", updatable = false)
    private String rejectDescription;
	

	public String getRejectCode() {
		return rejectCode;
	}

	public void setRejectCode(String rejectCode) {
		this.rejectCode = rejectCode;
	}

	public String getRejectDescription() {
		return rejectDescription;
	}

	public void setRejectDescription(String rejectDescription) {
		this.rejectDescription = rejectDescription;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (rejectCode != null ? rejectCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof QuoteRejectCode)) {
            return false;
        }
        QuoteRejectCode other = (QuoteRejectCode) object;
        if ((this.rejectCode == null && other.rejectCode != null) || (this.rejectCode != null && !this.rejectCode.equals(other.rejectCode))) {
            return false;
        }
        return true;
    }
}
