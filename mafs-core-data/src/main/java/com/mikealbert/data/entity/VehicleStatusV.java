package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Mapped to VEHICLE_STATUS_V view
 */
@Entity
@Table(name = "VEHICLE_STATUS_V")
public class VehicleStatusV implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="FMS_ID")
	private Long fmsId;
	
	@Column(name="VEHICLE_STATUS")
	private String vehicleStatus;

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}
}
