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
 * The persistent class for the MAINTENANCE_TABLES database table.
 * 
 */
@Entity
@Table(name="MAINTENANCE_TABLES")
public class MaintenanceTable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MTB_ID")
	private long mtbId;

	@Column(name="BMMM_BMMM_ID")
	private java.math.BigDecimal bmmmBmmmId;

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

	@Column(name="MBM_MBM_ID")
	private java.math.BigDecimal mbmMbmId;

	@Column(name="MDL_MDL_ID")
	private java.math.BigDecimal mdlMdlId;

	@Column(name="OAC_OAC_ID")
	private java.math.BigDecimal oacOacId;

	private String status;

	@Column(name="TABLE_CODE")
	private String tableCode;

	@Column(name="TYPE_OF_GRID")
	private String typeOfGrid;

    public MaintenanceTable() {
    }

	public long getMtbId() {
		return this.mtbId;
	}

	public void setMtbId(long mtbId) {
		this.mtbId = mtbId;
	}

	public java.math.BigDecimal getBmmmBmmmId() {
		return this.bmmmBmmmId;
	}

	public void setBmmmBmmmId(java.math.BigDecimal bmmmBmmmId) {
		this.bmmmBmmmId = bmmmBmmmId;
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

	public java.math.BigDecimal getMbmMbmId() {
		return this.mbmMbmId;
	}

	public void setMbmMbmId(java.math.BigDecimal mbmMbmId) {
		this.mbmMbmId = mbmMbmId;
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

	public String getTypeOfGrid() {
		return this.typeOfGrid;
	}

	public void setTypeOfGrid(String typeOfGrid) {
		this.typeOfGrid = typeOfGrid;
	}

	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.MaintenanceTable[ mtbId=" + mtbId + " ]";
    }

}