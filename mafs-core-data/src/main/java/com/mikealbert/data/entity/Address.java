package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Base class for ADDRESSES
 * @author Lizak
 */

@SuppressWarnings("serial")
@MappedSuperclass
public class Address extends BaseEntity implements Serializable {

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

    @MANotNull(label = "Zip Code")
    @MASize(label = "Zip Code", min = 1, max = 25)
    @Column(name = "POSTCODE")
    private String postcode;

    @MANotNull(label = "Geo Code")
    @MASize(label = "Geo Code", min = 1, max = 25)
    @Column(name = "GEO_CODE")
    private String geoCode;   

    @JoinColumns({
        @JoinColumn(name = "COUNTRY", referencedColumnName = "COUNTRY_CODE"),
        @JoinColumn(name = "REGION", referencedColumnName = "REGION_CODE"),
        @JoinColumn(name = "COUNTY_CODE", referencedColumnName = "COUNTY_CODE"),
        @JoinColumn(name = "TOWN_CITY", referencedColumnName = "TOWN_NAME")})
    @ManyToOne
    private TownCityCode townCityCode;

    @JoinColumn(name = "ADDRESS_TYPE", referencedColumnName = "ADDRESS_TYPE")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AddressTypeCode addressType;


    public Address() {
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

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public TownCityCode getTownCityCode() {
        return townCityCode;
    }

    public void setTownCityCode(TownCityCode townCityCode) {
        this.townCityCode = townCityCode;
    }

    public AddressTypeCode getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeCode addressType) {
        this.addressType = addressType;
    }

    public Country getCountry() {
    	if(this.townCityCode != null){
    		return getTownCityCode().getCountry();
    	}else{
    		return null;
    	}
    }

    public RegionCode getRegionCode() {
    	if(this.townCityCode != null){
    		return getTownCityCode().getRegionCode();
    	}else{
    		return null;
    	}
    }

    public CountyCode getCountyCode() {
    	if(this.townCityCode != null){
    		return getTownCityCode().getCountyCode();
    	}else{
    		return null;
    	}
    }

    public String getCityDescription() {
    	if(this.townCityCode != null){
    		return getTownCityCode().getTownDescription();
    	}else{
    		return null;
    	}
    }

    public String getRegionDescription() {
    	if(getRegionCode() != null){
    		return getRegionCode().getRegionDesc();
    	}else{
    		return null;
    	}
    }

    public String getRegionAbbreviation() {
    	if(getRegionCode() != null){
    		return getRegionCode().getRegionCodesPK().getRegionCode();
    	}else{
    		return null;
    	}
    }

}
