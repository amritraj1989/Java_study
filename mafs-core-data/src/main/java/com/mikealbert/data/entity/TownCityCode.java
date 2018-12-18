package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to TOWN_CITY_CODES table
 * @author sibley
 */
@Entity
@Table(name = "TOWN_CITY_CODES")
public class TownCityCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TownCityCodePK townCityCodesPK;

    @Size(max = 240)
    @Column(name = "TOWN_DESCRIPTION")
    private String townDescription;

    @Size(max = 10)
    @Column(name = "TOWN_CODE")
    private String townCode;

    @JoinColumns({
        @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "REGION_CODE", referencedColumnName = "REGION_CODE", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RegionCode regionCode;

    @JoinColumns({
        @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "REGION_CODE", referencedColumnName = "REGION_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "COUNTY_CODE", referencedColumnName = "COUNTY_CODE", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private CountyCode countyCode;

    @OneToMany(mappedBy = "townCityCode")
    private Collection<DriverAddressHistory> driverAddressesHistoryCollection;

    @OneToMany(mappedBy = "townCityCode")
    private Collection<CityZipCode> cityZipCodesCollection;

    @OneToMany(mappedBy = "townCityCode")
    private Collection<DriverAddress> driverAddressesCollection;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Country country;

    
    public TownCityCode() {
    }

    public TownCityCode(TownCityCodePK townCityCodesPK) {
        this.townCityCodesPK = townCityCodesPK;
    }

    public TownCityCode(String countryCode, String regionCode, String townName, String countyCode) {
        this.townCityCodesPK = new TownCityCodePK(countryCode, regionCode, townName, countyCode);
    }

    public TownCityCodePK getTownCityCodesPK() {
        return townCityCodesPK;
    }

    public void setTownCityCodesPK(TownCityCodePK townCityCodesPK) {
        this.townCityCodesPK = townCityCodesPK;
    }

    public String getTownDescription() {
        return townDescription;
    }

    public void setTownDescription(String townDescription) {
        this.townDescription = townDescription;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public RegionCode getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(RegionCode regionCode) {
        this.regionCode = regionCode;
    }

    public CountyCode getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(CountyCode countyCode) {
        this.countyCode = countyCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    
    public Collection<DriverAddressHistory> getDriverAddressesHistoryCollection() {
        return driverAddressesHistoryCollection;
    }

    public void setDriverAddressesHistoryCollection(Collection<DriverAddressHistory> driverAddressesHistoryCollection) {
        this.driverAddressesHistoryCollection = driverAddressesHistoryCollection;
    }

    public Collection<CityZipCode> getCityZipCodesCollection() {
        return cityZipCodesCollection;
    }

    public void setCityZipCodesCollection(Collection<CityZipCode> cityZipCodesCollection) {
        this.cityZipCodesCollection = cityZipCodesCollection;
    }

    public Collection<DriverAddress> getDriverAddressesCollection() {
        return driverAddressesCollection;
    }

    public void setDriverAddressesCollection(Collection<DriverAddress> driverAddressesCollection) {
        this.driverAddressesCollection = driverAddressesCollection;
    }        
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (townCityCodesPK != null ? townCityCodesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TownCityCode)) {
            return false;
        }
        TownCityCode other = (TownCityCode) object;
        if ((this.townCityCodesPK == null && other.townCityCodesPK != null) || (this.townCityCodesPK != null && !this.townCityCodesPK.equals(other.townCityCodesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.TownCityCodes[ townCityCodesPK=" + townCityCodesPK + " ]";
    }

}
