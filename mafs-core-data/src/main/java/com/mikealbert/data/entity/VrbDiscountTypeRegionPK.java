package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the VRB_DISCOUNT_TYPE_REGIONS database table.
 * 
 */
@Embeddable
public class VrbDiscountTypeRegionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="VDTF_VDTF_ID")
	private long vdtfVdtfId;

	private String region;

    public VrbDiscountTypeRegionPK() {
    }
	public long getVdtfVdtfId() {
		return this.vdtfVdtfId;
	}
	public void setVdtfVdtfId(long vdtfVdtfId) {
		this.vdtfVdtfId = vdtfVdtfId;
	}
	public String getRegion() {
		return this.region;
	}
	public void setRegion(String region) {
		this.region = region;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof VrbDiscountTypeRegionPK)) {
			return false;
		}
		VrbDiscountTypeRegionPK castOther = (VrbDiscountTypeRegionPK)other;
		return 
			(this.vdtfVdtfId == castOther.vdtfVdtfId)
			&& this.region.equals(castOther.region);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.vdtfVdtfId ^ (this.vdtfVdtfId >>> 32)));
		hash = hash * prime + this.region.hashCode();
		
		return hash;
    }
}