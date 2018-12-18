package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the DELIV_SHIP_DETAILS database table.
 * 
 */
@Entity
@Table(name="DELIV_SHIP_DETAILS")
public class DeliveryShipDetail implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DD_ID")
    @SequenceGenerator(name = "DD_ID", sequenceName = "DD_ID", allocationSize = 1)
    @Column(name = "DD_ID")
    private Long ddId;
    
	@Column(name="DEL_ADD_1")
	private String delAddressLine1;

	@Column(name="DEL_ADD_2")
	private String delAddressLine2;
	
	@Column(name="TOWN_CITY")
	private String townCity;
	
	@Column(name="REGION")
	private String region;
	
	@Column(name="DEL_POST_CODE")
	private String postalCode;
	
	@Column(name="DOC_ID")
	private Long docId;
	
	@Column(name="COUNTRY")
	private String country;

	public Long getDdId() {
		return ddId;
	}

	public void setDdId(Long ddId) {
		this.ddId = ddId;
	}

	public String getDelAddressLine1() {
		return delAddressLine1;
	}

	public void setDelAddressLine1(String delAddressLine1) {
		this.delAddressLine1 = delAddressLine1;
	}

	public String getDelAddressLine2() {
		return delAddressLine2;
	}

	public void setDelAddressLine2(String delAddressLine2) {
		this.delAddressLine2 = delAddressLine2;
	}

	public String getTownCity() {
		return townCity;
	}

	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	

}