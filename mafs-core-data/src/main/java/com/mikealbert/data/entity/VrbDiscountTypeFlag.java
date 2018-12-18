package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the VRB_DISCOUNT_TYPE_FLAGS database table.
 * 
 */
@Entity
@Table(name="VRB_DISCOUNT_TYPE_FLAGS")
public class VrbDiscountTypeFlag extends BaseEntity  implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VDTF_ID")
	private long vdtfId;

	@Column(name="DESCRIPTION_CONTENT")
	private String descriptionContent;

	private String flags;

	private String regions;

	//bi-directional many-to-one association to VrbDiscountTypeCode
    @ManyToOne
	@JoinColumn(name="VRB_DISCOUNT_TYPE")
	private VrbDiscountTypeCode vrbDiscountTypeCode;

	//bi-directional many-to-one association to VrbDiscountTypeRegion
	@OneToMany(mappedBy="vrbDiscountTypeFlag")
	private Set<VrbDiscountTypeRegion> vrbDiscountTypeRegions;

    public VrbDiscountTypeFlag() {
    }

	public long getVdtfId() {
		return this.vdtfId;
	}

	public void setVdtfId(long vdtfId) {
		this.vdtfId = vdtfId;
	}

	public String getDescriptionContent() {
		return this.descriptionContent;
	}

	public void setDescriptionContent(String descriptionContent) {
		this.descriptionContent = descriptionContent;
	}

	public String getFlags() {
		return this.flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public String getRegions() {
		return this.regions;
	}

	public void setRegions(String regions) {
		this.regions = regions;
	}

	public VrbDiscountTypeCode getVrbDiscountTypeCode() {
		return this.vrbDiscountTypeCode;
	}

	public void setVrbDiscountTypeCode(VrbDiscountTypeCode vrbDiscountTypeCode) {
		this.vrbDiscountTypeCode = vrbDiscountTypeCode;
	}
	
	public Set<VrbDiscountTypeRegion> getVrbDiscountTypeRegions() {
		return this.vrbDiscountTypeRegions;
	}

	public void setVrbDiscountTypeRegions(Set<VrbDiscountTypeRegion> vrbDiscountTypeRegions) {
		this.vrbDiscountTypeRegions = vrbDiscountTypeRegions;
	}
	
}