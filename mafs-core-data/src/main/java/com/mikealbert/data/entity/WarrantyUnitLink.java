package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;


/**
 * Mapped to WARRANTY_UNIT_LINKS table
 * @author Raj
 */
@Entity
@Table(name="WARRANTY_UNIT_LINKS")
public class WarrantyUnitLink extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WUL_SEQ")
	@SequenceGenerator(name="WUL_SEQ", sequenceName="WUL_SEQ", allocationSize=1)
	@NotNull
	@Column(name="WUL_ID")
	private long wulId;
	
	@Temporal(TemporalType.DATE)
	@NotNull
	@Column(name="END_DATE")
	private Date endDate;

	@Temporal(TemporalType.DATE)
	@NotNull
	@Column(name="START_DATE")
	private Date startDate;

	//bi-directional many-to-one association to FleetMaster
	@ManyToOne
	@NotNull
	@JoinColumn(name="FMS_FMS_ID")
	private FleetMaster fleetMaster;

	//bi-directional many-to-one association to WarrantyDetail
	@ManyToOne (optional = false)
	@JoinColumn(name="WD_WD_ID")
	private WarrantyDetail warrantyDetail;

	public long getWulId() {
		return wulId;
	}

	public void setWulId(long wulId) {
		this.wulId = wulId;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public FleetMaster getFleetMaster() {
		return this.fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	
	public WarrantyDetail getWarrantyDetail() {
		return this.warrantyDetail;
	}

	public void setWarrantyDetail(WarrantyDetail warrantyDetail) {
		this.warrantyDetail = warrantyDetail;
	}

	
}