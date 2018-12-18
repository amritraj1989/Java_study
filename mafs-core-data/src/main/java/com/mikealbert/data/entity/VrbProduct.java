package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the VRB_PRODUCTS database table.
 * 
 */
@Entity
@Table(name="VRB_PRODUCTS")
public class VrbProduct extends BaseEntity  implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VRBP_ID")
	private long vrbpId;

	@Column(name="PRODUCT_CODE")
	private String productCode;

	//bi-directional many-to-one association to VrbDiscount
    @ManyToOne
	@JoinColumn(name="VRBD_VRBD_ID")
	private VrbDiscount vrbDiscount;

    public VrbProduct() {
    }

	public long getVrbpId() {
		return this.vrbpId;
	}

	public void setVrbpId(long vrbpId) {
		this.vrbpId = vrbpId;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public VrbDiscount getVrbDiscount() {
		return this.vrbDiscount;
	}

	public void setVrbDiscount(VrbDiscount vrbDiscount) {
		this.vrbDiscount = vrbDiscount;
	}
	
}