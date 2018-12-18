package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DOCL database table.
 * @author Singh
 */
@Embeddable
public class DoclPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DOC_ID")
	private long docId;

	@Column(name="LINE_ID")
	private long lineId;

    public DoclPK() {
    }
    public DoclPK(long docId ,long lineId ) {
    	this.docId = docId;
    	this.lineId = lineId;
    }
	public long getDocId() {
		return this.docId;
	}
	public void setDocId(long docId) {
		this.docId = docId;
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
		if (!(other instanceof DoclPK)) {
			return false;
		}
		DoclPK castOther = (DoclPK)other;
		return 
			(this.docId == castOther.docId)
			&& (this.lineId == castOther.lineId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.docId ^ (this.docId >>> 32)));
		hash = hash * prime + ((int) (this.lineId ^ (this.lineId >>> 32)));
		
		return hash;
    }
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.DOCL.DoclPK[ docId=" + docId + " and lineId"+lineId +" ]";
    }
}