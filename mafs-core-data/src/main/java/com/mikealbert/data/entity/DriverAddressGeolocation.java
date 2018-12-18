package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "DRIVER_ADDRESS_GEOLOCATIONS")
public class DriverAddressGeolocation implements Serializable {
	private static final long serialVersionUID = 282358503353721984L;

	@Id
	@NotNull
	@Column(name = "DRA_DRA_ID")
	private Long draId;
	
	@Column(name = "LATITUDE")
	private String latitude;
	
	@Column(name = "LONGITUDE")
	private String longitude;
	
	@NotNull
	@Column(name = "GEOCODE_SERVICE")
	private String geocodeService;
	
	@NotNull
	@Column(name = "DATE_GEOCODED")
	private Date dateGeocoded;
	
	@Column(name = "LOCATION_TYPE")
	private String locationType;
	
	@Column(name = "SERVICE_QUALITY_SCORE")
	private String serviceQualityScore;
	
	@Column(name = "MAFS_QUALITY_SCORE")
	private Long mafsQualityScore;
	
	@Column(name = "VERIFIED_DATE")
	private Date verifiedDate;

	@JoinColumn(name = "DRA_DRA_ID", referencedColumnName = "DRA_ID")
	@OneToOne(optional = false, fetch = FetchType.EAGER)
	private DriverAddress driverAddress;

	public Long getDraId() {
		return draId;
	}

	public void setDraId(Long draId) {
		this.draId = draId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getGeocodeService() {
		return geocodeService;
	}

	public void setGeocodeService(String geocodeService) {
		this.geocodeService = geocodeService;
	}

	public Date getDateGeocoded() {
		return dateGeocoded;
	}

	public void setDateGeocoded(Date dateGeocoded) {
		this.dateGeocoded = dateGeocoded;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getServiceQualityScore() {
		return serviceQualityScore;
	}

	public void setServiceQualityScore(String serviceQualityScore) {
		this.serviceQualityScore = serviceQualityScore;
	}

	public Long getMafsQualityScore() {
		return mafsQualityScore;
	}

	public void setMafsQualityScore(Long mafsQualityScore) {
		this.mafsQualityScore = mafsQualityScore;
	}

	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public DriverAddress getDriverAddress() {
		return this.driverAddress;
	}

	public void setDriverAddress(DriverAddress driverAddress) {
		this.driverAddress = driverAddress;
	}
}
