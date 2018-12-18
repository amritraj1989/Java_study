package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the MT_IF_DETAIL database table.
 * 
 */
@Embeddable
public class ServiceProviderInvoiceDetailPK implements Serializable {
	private static final long serialVersionUID = 741679444475560777L;
	
	private long recordId;
	
	@Column(name="LINE_ID")
	private long lineId;

	public ServiceProviderInvoiceDetailPK() {
	}

	public long getRecordId() {
		return this.recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}


	public long getLineId() {
		return this.lineId;
	}
	public void setLineId(long lineId) {
		this.lineId = lineId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ServiceProviderInvoiceDetailPK)) {
			return false;
		}
		ServiceProviderInvoiceDetailPK castOther = (ServiceProviderInvoiceDetailPK)other;
		return 
			(this.recordId == castOther.recordId)
			&& (this.lineId == castOther.lineId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.recordId ^ (this.recordId >>> 32)));
		hash = hash * prime + ((int) (this.lineId ^ (this.lineId >>> 32)));
		
		return hash;
	}
}