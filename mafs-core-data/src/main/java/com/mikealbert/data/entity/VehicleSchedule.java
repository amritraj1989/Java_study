package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The persistent class for the VEHCILE_SCHEDULES database table.
 */
@Entity
@Table(name = "VEHICLE_SCHEDULES")
public class VehicleSchedule extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8367018184519217139L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VSCH_ID_SEQ")
	@SequenceGenerator(name = "VSCH_ID_SEQ", sequenceName = "VSCH_ID_SEQ", allocationSize = 1)
	@NotNull
	@Column(name = "VSCH_ID")
	private Long vschId;

	@Column(name = "ACTIVE_FROM")
	private Date activeFrom;

	@Column(name = "ACTIVE_TO")
	private Date activeTo;
	
	@Column(name = "VEH_SCH_SEQ")
	private	Long	vehSchSeq;
	
	@Column(name = "STARTING_ODO_READING")
	private	Long	startOdoReading;
	
	@JoinColumn(name = "MSCH_MSCH_ID", referencedColumnName = "MSCH_ID")
	@ManyToOne
	private MasterSchedule masterSchedule;

	@JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
	@ManyToOne
	private FleetMaster fleetMaster;

	@OneToMany(mappedBy = "vehicleSchedule", cascade = CascadeType.ALL,fetch = FetchType.EAGER, orphanRemoval = true)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<VehicleScheduleInterval> vehicleScheduleIntervals;

	public VehicleSchedule() {
	}

	public Long getVschId() {
		return vschId;
	}

	public void setVschId(Long vschId) {
		this.vschId = vschId;
	}

	public MasterSchedule getMasterSchedule() {
		return masterSchedule;
	}

	public void setMasterSchedule(MasterSchedule masterSchedule) {
		this.masterSchedule = masterSchedule;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Date getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

	

	public Long getVehSchSeq() {
		return vehSchSeq;
	}

	public void setVehSchSeq(Long vehSchSeq) {
		this.vehSchSeq = vehSchSeq;
	}

	public List<VehicleScheduleInterval> getVehicleScheduleIntervals() {
		return vehicleScheduleIntervals;
	}

	public void setVehicleScheduleIntervals(List<VehicleScheduleInterval> vehicleScheduleIntervals) {
		this.vehicleScheduleIntervals = vehicleScheduleIntervals;
	}
	
	
	public void addVehicleScheduleIntervals(VehicleScheduleInterval vehicleScheduleInterval) {
		this.vehicleScheduleIntervals.add(vehicleScheduleInterval);
	}

	public Long getStartOdoReading() {
		return startOdoReading;
	}

	public void setStartOdoReading(Long startOdoReading) {
		this.startOdoReading = startOdoReading;
	}

	@Override
	public String toString() {
		return "com.mikealbert.data.entity.VehcileSchedule[ vschId=" + this.getVschId() + " ]";
	}
	
}