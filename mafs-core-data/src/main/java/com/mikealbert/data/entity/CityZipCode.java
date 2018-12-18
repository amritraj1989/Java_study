package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 * Mapped to the CITY_ZIP_CODES table
 * @author sibley
 */
@Entity
@Table(name = "CITY_ZIP_CODES")
public class CityZipCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected CityZipCodePK cityZipCodePK;

    @Size(max = 25)
    @Column(name = "ZIP_CODE_END")
    private String zipCodeEnd;

    @Size(max = 25)
    @Column(name = "GEO_CODE")
    private String geoCode;

    @JoinColumns({
        @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "REGION_CODE", referencedColumnName = "REGION_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "COUNTY_CODE", referencedColumnName = "COUNTY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "CITY_CODE", referencedColumnName = "TOWN_NAME", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TownCityCode townCityCode;

    public CityZipCode() {
    }

    public CityZipCode(CityZipCodePK cityZipCodePK) {
        this.cityZipCodePK = cityZipCodePK;
    }

    public CityZipCode(String countryCode, String regionCode, String countyCode, String cityCode, String zipCode) {
        this.cityZipCodePK = new CityZipCodePK(countryCode, regionCode, countyCode, cityCode, zipCode);
    }

    public CityZipCodePK getCityZipCodePK() {
        return cityZipCodePK;
    }

    public void setCityZipCodePK(CityZipCodePK cityZipCodePK) {
        this.cityZipCodePK = cityZipCodePK;
    }

    public String getZipCodeEnd() {
        return zipCodeEnd;
    }

    public void setZipCodeEnd(String zipCodeEnd) {
        this.zipCodeEnd = zipCodeEnd;
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
    
    @Transient
    public String getUniqueUIKey() {
    	
    	return getTownCityCode().getTownDescription() + "-" + getTownCityCode().getCountyCode().getCountyName() + "-" +  getGeoCode();
    }
    
    @Transient
    public String getFullGeoCode() {
    	return getTownCityCode().getRegionCode().getShortCode() + "-" + getCityZipCodePK().getCountyCode() + "-" + getGeoCode();
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cityZipCodePK != null ? cityZipCodePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CityZipCode)) {
            return false;
        }
        CityZipCode other = (CityZipCode) object;
        if ((this.cityZipCodePK == null && other.cityZipCodePK != null) || (this.cityZipCodePK != null && !this.cityZipCodePK.equals(other.cityZipCodePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.CityZipCode[ cityZipCodePK=" + cityZipCodePK + " ]";
    }

}
