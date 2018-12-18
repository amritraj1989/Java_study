package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Mapped to UNIT_REGISTRATIONS_V view
 */
@Entity
@Table(name = "UNIT_REGISTRATIONS_V")
public class VehicleRegistrationV implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="FMS_FMS_ID")
	private Long fmsId;

	@Size(min = 1, max = 30)
	@Column(name="VIN")
	private String vin;
	
	@Size(min = 1, max = 25)
	@Column(name="UNIT_NO")
	private String unitNo;
	
	@Size(min = 1, max = 20)
	@Column(name="LIC_PLATE_NO")
	private String licensePlateNo;
	
	@Column(name="ISSUED_DATE")
	private Date issuedDate;
	
	@Column(name="PLATE_EXPIRY_DATE")
	private Date expirationDate;
	
	@Size(min = 1, max = 80)
	@Column(name="REGION_CODE")
	private String licensePlateState;
		
	public VehicleRegistrationV(){}
	
	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getLicensePlateNo() {
		return licensePlateNo;
	}

	public void setLicensePlateNo(String licensePlateNo) {
		this.licensePlateNo = licensePlateNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getLicensePlateState() {
		return licensePlateState;
	}

	public void setLicensePlateState(String licensePlateState) {
		this.licensePlateState = licensePlateState;
	}
}
