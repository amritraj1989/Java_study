package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;


/**
 * The persistent class for the SUPPLIER_ADDRESSES database table.
 * 
 * Note: Service Provider Addresses cannot extend the base entity of Address because 
 * some historical data exists in supplier_addresses table but does not exist in 
 * the town_city_codes table (an extension of Address Entity). 
 * 
 */
@Entity
@Table(name="SUPPLIER_ADDRESSES")
public class ServiceProviderAddress implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SUPPLIER_ADDRESSES_ID_GENERATOR", sequenceName="SUA_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUPPLIER_ADDRESSES_ID_GENERATOR")
	@Column(name="SUA_ID", unique=true, nullable=false, precision=12)
	private Long suaId;
	
	@Column(name="DEFAULT_IND", length=1)
	private String defaultInd;

	@JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID", nullable=false)
	@ManyToOne(optional = true)
	private ServiceProvider serviceProvider;
	
	@JoinColumn(name="SUA_ID", referencedColumnName = "SUA_SUA_ID")
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	private ServiceProviderAddressGeolocation serviceProviderAddressGeolocation;
	
	@Column(name = "ADDRESS_TYPE")
	private String addressType;
		
	@Size(max = 25)
    @Column(name = "STREET_NO")
    private String streetNo;

	@MANotNull(label = "Address 1")
    @MASize(label = "Address 1", min = 1, max = 80)
    @Column(name = "ADDRESS_LINE_1")
    private String addressLine1;
	
    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_2")
    private String addressLine2;
    
    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_3")
    private String addressLine3;

    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_4")
    private String addressLine4;
    
    @MANotNull(label = "Zip Code")
    @MASize(label = "Zip Code", min = 1, max = 25)
    @Column(name = "POSTCODE")
    private String postcode;

    @Basic(optional = false)
    @MANotNull(label = "Town")
    @MASize(label = "Town", min = 1, max = 80)
    @Column(name = "TOWN_CITY")
    private String townCity;
    
    @Basic(optional = false)
    @MANotNull(label = "State")
    @MASize(label = "State", min = 1, max = 80)
    @Column(name = "REGION")
    private String region;
    
    @Basic(optional = false)
    @MANotNull(label = "Country")
    @MASize(label = "Country", min = 1, max = 80)
    @Column(name = "COUNTRY")
    private String countryCode;
    
    @Size(max = 25)
    @Column(name = "GEO_CODE")
    private String geoCode; 
    
    @Size(max = 10)
    @Column(name = "COUNTY_CODE")
    private String countyCode;
	
	
	public ServiceProviderAddress() {
	}

	public Long getSuaId() {
		return this.suaId;
	}

	public void setSuaId(Long suaId) {
		this.suaId = suaId;
	}	

    public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

    public ServiceProviderAddressGeolocation getServiceProviderAddressGeolocation() {
    	return this.serviceProviderAddressGeolocation;
    }
    
    public void setServiceProviderAddressGeolocation(ServiceProviderAddressGeolocation serviceProviderAddressGeolocation) {
    	this.serviceProviderAddressGeolocation = serviceProviderAddressGeolocation;
    }	
	
	public String getDefaultInd() {
		return this.defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	
}