package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the RESIDUAL_TABLES database table.
 * 
 */
@Entity
@Table(name="RESIDUAL_TABLES")
public class ResidualTable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RTB_ID")
	private long rtbId;

	@Column(name="BMV_BMV_ID")
	private java.math.BigDecimal bmvBmvId;

	@Column(name="DAC_DAC_ID")
	private java.math.BigDecimal dacDacId;

	@Column(name="DISTANCE_UOM")
	private String distanceUom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM")
	private Date effectiveFrom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_TO")
	private Date effectiveTo;

	@Column(name="MDL_MDL_ID")
	private java.math.BigDecimal mdlMdlId;

	@Column(name="OAC_OAC_ID")
	private java.math.BigDecimal oacOacId;

	@Column(name="OPH_OPH_ID")
	private java.math.BigDecimal ophOphId;

	@Column(name="RBM_RBM_ID")
	private java.math.BigDecimal rbmRbmId;

	private String status;

	@Column(name="TABLE_CODE")
	private String tableCode;

    public ResidualTable() {
    }

	public long getRtbId() {
		return this.rtbId;
	}

	public void setRtbId(long rtbId) {
		this.rtbId = rtbId;
	}

	public java.math.BigDecimal getBmvBmvId() {
		return this.bmvBmvId;
	}

	public void setBmvBmvId(java.math.BigDecimal bmvBmvId) {
		this.bmvBmvId = bmvBmvId;
	}

	public java.math.BigDecimal getDacDacId() {
		return this.dacDacId;
	}

	public void setDacDacId(java.math.BigDecimal dacDacId) {
		this.dacDacId = dacDacId;
	}

	public String getDistanceUom() {
		return this.distanceUom;
	}

	public void setDistanceUom(String distanceUom) {
		this.distanceUom = distanceUom;
	}

	public Date getEffectiveFrom() {
		return this.effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return this.effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public java.math.BigDecimal getMdlMdlId() {
		return this.mdlMdlId;
	}

	public void setMdlMdlId(java.math.BigDecimal mdlMdlId) {
		this.mdlMdlId = mdlMdlId;
	}

	public java.math.BigDecimal getOacOacId() {
		return this.oacOacId;
	}

	public void setOacOacId(java.math.BigDecimal oacOacId) {
		this.oacOacId = oacOacId;
	}

	public java.math.BigDecimal getOphOphId() {
		return this.ophOphId;
	}

	public void setOphOphId(java.math.BigDecimal ophOphId) {
		this.ophOphId = ophOphId;
	}

	public java.math.BigDecimal getRbmRbmId() {
		return this.rbmRbmId;
	}

	public void setRbmRbmId(java.math.BigDecimal rbmRbmId) {
		this.rbmRbmId = rbmRbmId;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTableCode() {
		return this.tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}


	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.ResidualTable[ rtbId=" + rtbId + " ]";
    }
}