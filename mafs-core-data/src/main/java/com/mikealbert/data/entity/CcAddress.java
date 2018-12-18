package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the CC_ADDRESSES database table.
 * 
 */
@Entity
@Table(name="CC_ADDRESSES")
@NamedQuery(name="CcAddress.findAll", query="SELECT c FROM CcAddress c")
public class CcAddress implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CCA_ID")
	private Long ccaId;

	@Column(name="ADDRESS_LINE_1")
	private String addressLine1;

	@Column(name="ADDRESS_LINE_2")
	private String addressLine2;

	@Column(name="ADDRESS_LINE_3")
	private String addressLine3;

	@Column(name="ADDRESS_LINE_4")
	private String addressLine4;

	@Column(name="ADDRESS_TYPE")
	private String addressType;

	@Column(name="C_ID")
	private long cId;

	private String country;

	@Column(name="COUNTY_CODE")
	private String countyCode;

	@Column(name="DEFAULT_IND")
	private String defaultInd;

	@Column(name="GEO_CODE")
	private String geoCode;

	private String postcode;

	private String region;

	@Column(name="STREET_NO")
	private String streetNo;

	@Column(name="TOWN_CITY")
	private String townCity;

	public CcAddress() {
	}

	public long getCcaId() {
		return this.ccaId;
	}

	public void setCcaId(long ccaId) {
		this.ccaId = ccaId;
	}

	public String getAddressLine1() {
		return this.addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return this.addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return this.addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return this.addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressType() {
		return this.addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public long getCId() {
		return this.cId;
	}

	public void setCId(long cId) {
		this.cId = cId;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountyCode() {
		return this.countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getDefaultInd() {
		return this.defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public String getGeoCode() {
		return this.geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreetNo() {
		return this.streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getTownCity() {
		return this.townCity;
	}

	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}

}