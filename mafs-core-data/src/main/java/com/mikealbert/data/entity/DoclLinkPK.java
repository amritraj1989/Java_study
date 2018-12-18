package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DOCL_LINKS database table.
 * 
 */
@Embeddable
public class DoclLinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final Long serialVersionUID = 1L;

	@Column(name="PARENT_DOC_ID")
	private Long parentDocId;

	@Column(name="PARENT_LINE_ID")
	private Long parentLineId;

	@Column(name="CHILD_DOC_ID")
	private Long childDocId;

	@Column(name="CHILD_LINE_ID")
	private Long childLineId;

	public DoclLinkPK() {
	}
	public Long getParentDocId() {
		return this.parentDocId;
	}
	public void setParentDocId(Long parentDocId) {
		this.parentDocId = parentDocId;
	}
	public Long getParentLineId() {
		return this.parentLineId;
	}
	public void setParentLineId(Long parentLineId) {
		this.parentLineId = parentLineId;
	}
	public Long getChildDocId() {
		return this.childDocId;
	}
	public void setChildDocId(Long childDocId) {
		this.childDocId = childDocId;
	}
	public Long getChildLineId() {
		return this.childLineId;
	}
	public void setChildLineId(Long childLineId) {
		this.childLineId = childLineId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DoclLinkPK)) {
			return false;
		}
		DoclLinkPK castOther = (DoclLinkPK)other;
		return 
			(this.parentDocId == castOther.parentDocId)
			&& (this.parentLineId == castOther.parentLineId)
			&& (this.childDocId == castOther.childDocId)
			&& (this.childLineId == castOther.childLineId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.parentDocId ^ (this.parentDocId >>> 32)));
		hash = hash * prime + ((int) (this.parentLineId ^ (this.parentLineId >>> 32)));
		hash = hash * prime + ((int) (this.childDocId ^ (this.childDocId >>> 32)));
		hash = hash * prime + ((int) (this.childLineId ^ (this.childLineId >>> 32)));
		
		return hash;
	}
}