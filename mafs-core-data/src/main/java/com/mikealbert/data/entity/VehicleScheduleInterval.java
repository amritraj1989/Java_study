package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "VEHICLE_SCHEDULES_INTERVALS")
public class VehicleScheduleInterval extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VSI_ID_SEQ")
	@SequenceGenerator(name = "VSI_ID_SEQ", sequenceName = "VSI_ID_SEQ", allocationSize = 1)
	@Column(name = "VSI_ID")
	private Long vehSchIntervalId;

	@Column(name = "AUTHORIZATION_NO")
	private String authorizationNo;

	@Column(name = "DOC_ID")
	private Long docId;

	@Column(name = "DOC_NO")
	private String docNo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "VSCH_VSCH_ID")
	private VehicleSchedule vehicleSchedule;
	
	public Long getVehSchIntervalId() {
		return vehSchIntervalId;
	}

	public void setVehSchIntervalId(Long vehSchIntervalId) {
		this.vehSchIntervalId = vehSchIntervalId;
	}

	public VehicleSchedule getVehicleSchedule() {
		return vehicleSchedule;
	}

	public void setVehicleSchedule(VehicleSchedule vehicleSchedule) {
		this.vehicleSchedule = vehicleSchedule;
	}

	public String getAuthorizationNo() {
		return authorizationNo;
	}

	public void setAuthorizationNo(String authorizationNo) {
		this.authorizationNo = authorizationNo;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
}
