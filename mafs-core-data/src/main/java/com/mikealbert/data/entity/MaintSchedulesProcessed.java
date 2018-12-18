package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "MAINT_SCHEDULES_PROCESSED")
public class MaintSchedulesProcessed extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MSP_SEQ")    
    @SequenceGenerator(name="MSP_SEQ", sequenceName="MSP_SEQ", allocationSize=1)	
	@Column(name = "MSP_ID")
	private Long mspId;

	@JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
	@ManyToOne(fetch = FetchType.EAGER)
	private FleetMaster fleetMaster;

	@JoinColumn(name = "VSCH_VSCH_ID", referencedColumnName = "VSCH_ID")
	@ManyToOne(fetch = FetchType.EAGER)
	private VehicleSchedule vehicleSchedule;

	@Column(name = "EXPECTED_PRINT_DATE")
	private Date expectedPrintDate;
	
	@Column(name = "DATE_GENERATED")
	private Date dateGenerated;

	@JoinColumn(name = "ERROR_CODE", referencedColumnName = "ERROR_CODE")
	@ManyToOne(fetch = FetchType.EAGER)
	private ErrorCode errorCode;
	
	@JoinColumn(name = "BATCH_ID", referencedColumnName = "BATCH_ID")
	@ManyToOne(fetch = FetchType.EAGER)
	private MaintSchedulesBatch maintSchedulesBatch;

	

	public Long getMspId() {
		return mspId;
	}

	public void setMspId(Long mspId) {
		this.mspId = mspId;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public VehicleSchedule getVehicleSchedule() {
		return vehicleSchedule;
	}

	public void setVehicleSchedule(VehicleSchedule vehicleSchedule) {
		this.vehicleSchedule = vehicleSchedule;
	}

	public Date getExpectedPrintDate() {
		return expectedPrintDate;
	}

	public void setExpectedPrintDate(Date expectedPrintDate) {
		this.expectedPrintDate = expectedPrintDate;
	}

	
	
	public Date getDateGenerated() {
		return dateGenerated;
	}

	public void setDateGenerated(Date dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	
	
	public MaintSchedulesBatch getMaintSchedulesBatch() {
		return maintSchedulesBatch;
	}

	public void setMaintSchedulesBatch(MaintSchedulesBatch maintSchedulesBatch) {
		this.maintSchedulesBatch = maintSchedulesBatch;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (mspId != null ? mspId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof MaintSchedulesProcessed)) {
			return false;
		}
		MaintSchedulesProcessed other = (MaintSchedulesProcessed) object;
		if ((this.mspId == null && other.mspId != null)
				|| (this.mspId != null && !this.mspId.equals(other.mspId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.mikealbert.entity.MaintScheduleProcessed[ sequenceNumber=" + mspId + " ]";
	}

}
