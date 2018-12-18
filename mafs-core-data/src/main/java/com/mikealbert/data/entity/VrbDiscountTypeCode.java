package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;


/**
 * The persistent class for the VRB_DISCOUNT_TYPE_CODES database table.
 * 
 */
@Entity
@Table(name="VRB_DISCOUNT_TYPE_CODES")
public class VrbDiscountTypeCode  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VRB_TYPE_CODE")
	private String vrbTypeCode;

	@Column(name="ASC_FLAG_INDEX")
	private BigDecimal ascFlagIndex;

	private String description;

	//bi-directional many-to-one association to VrbDiscount
	@OneToMany(mappedBy="vrbDiscountTypeCode")
	private Set<VrbDiscount> vrbDiscounts;

	//bi-directional many-to-one association to VrbDiscountTypeFlag
	@OneToMany(mappedBy="vrbDiscountTypeCode")
	private Set<VrbDiscountTypeFlag> vrbDiscountTypeFlags;

    public VrbDiscountTypeCode() {
    }

	public String getVrbTypeCode() {
		return this.vrbTypeCode;
	}

	public void setVrbTypeCode(String vrbTypeCode) {
		this.vrbTypeCode = vrbTypeCode;
	}

	public BigDecimal getAscFlagIndex() {
		return this.ascFlagIndex;
	}

	public void setAscFlagIndex(BigDecimal ascFlagIndex) {
		this.ascFlagIndex = ascFlagIndex;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<VrbDiscount> getVrbDiscounts() {
		return this.vrbDiscounts;
	}

	public void setVrbDiscounts(Set<VrbDiscount> vrbDiscounts) {
		this.vrbDiscounts = vrbDiscounts;
	}
	
	public Set<VrbDiscountTypeFlag> getVrbDiscountTypeFlags() {
		return this.vrbDiscountTypeFlags;
	}

	public void setVrbDiscountTypeFlags(Set<VrbDiscountTypeFlag> vrbDiscountTypeFlags) {
		this.vrbDiscountTypeFlags = vrbDiscountTypeFlags;
	}
	
}