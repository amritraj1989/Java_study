package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the VRB_DISCOUNT_TYPE_REGIONS database table.
 * 
 */
@Entity
@Table(name="VRB_DISCOUNT_TYPE_REGIONS")
public class VrbDiscountTypeRegion extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private VrbDiscountTypeRegionPK id;

	//bi-directional many-to-one association to VrbDiscountTypeFlag
  @ManyToOne
	@JoinColumn(name="VDTF_VDTF_ID", insertable =false, updatable= false)
	private VrbDiscountTypeFlag vrbDiscountTypeFlag;

    public VrbDiscountTypeRegion() {
    }

	public VrbDiscountTypeRegionPK getId() {
		return this.id;
	}

	public void setId(VrbDiscountTypeRegionPK id) {
		this.id = id;
	}
	
	
	
}