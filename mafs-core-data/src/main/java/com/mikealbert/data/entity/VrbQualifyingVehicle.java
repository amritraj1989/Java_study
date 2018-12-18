package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the VRB_QUALIFYING_VEHICLES database table.
 * 
 */
@Entity
@Table(name="VRB_QUALIFYING_VEHICLES")
public class VrbQualifyingVehicle extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VQV_ID")
	private long vqvId;

	@Column(name="MDL_MDL_ID")
	private java.math.BigDecimal mdlMdlId;

	@Column(name="VRB_TABLE_NO")
	private String vrbTableNo;

    public VrbQualifyingVehicle() {
    }

	public long getVqvId() {
		return this.vqvId;
	}

	public void setVqvId(long vqvId) {
		this.vqvId = vqvId;
	}

	public java.math.BigDecimal getMdlMdlId() {
		return this.mdlMdlId;
	}

	public void setMdlMdlId(java.math.BigDecimal mdlMdlId) {
		this.mdlMdlId = mdlMdlId;
	}

	public String getVrbTableNo() {
		return this.vrbTableNo;
	}

	public void setVrbTableNo(String vrbTableNo) {
		this.vrbTableNo = vrbTableNo;
	}

}