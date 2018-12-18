package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to COUNTY_CODES table
 * @author sibley
 */
@Entity
@Table(name = "COUNTY_CODES")
public class CountyCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected CountyCodePK countyCodesPK;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "COUNTY_NAME")
    private String countyName;

    @OneToMany(mappedBy = "countyCode")
    private Collection<TownCityCode> townCityCodesCollection;

    @JoinColumns({
        @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "REGION_CODE", referencedColumnName = "REGION_CODE", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RegionCode regionCode;

    public CountyCode() {
    }

    public CountyCode(CountyCodePK countyCodesPK) {
        this.countyCodesPK = countyCodesPK;
    }

    public CountyCode(CountyCodePK countyCodesPK, String countyName) {
        this.countyCodesPK = countyCodesPK;
        this.countyName = countyName;
    }

    public CountyCode(String countryCode, String regionCode, String countyCode) {
        this.countyCodesPK = new CountyCodePK(countryCode, regionCode, countyCode);
    }

    public CountyCodePK getCountyCodesPK() {
        return countyCodesPK;
    }

    public void setCountyCodesPK(CountyCodePK countyCodesPK) {
        this.countyCodesPK = countyCodesPK;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public Collection<TownCityCode> getTownCityCodesCollection() {
        return townCityCodesCollection;
    }

    public void setTownCityCodesCollection(Collection<TownCityCode> townCityCodesCollection) {
        this.townCityCodesCollection = townCityCodesCollection;
    }

    public RegionCode getRegionCode() {
        return regionCode;
    }

    public void setRegionCodes(RegionCode regionCode) {
        this.regionCode = regionCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (countyCodesPK != null ? countyCodesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CountyCode)) {
            return false;
        }
        CountyCode other = (CountyCode) object;
        if ((this.countyCodesPK == null && other.countyCodesPK != null) || (this.countyCodesPK != null && !this.countyCodesPK.equals(other.countyCodesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.CountyCodes[ countyCodesPK=" + countyCodesPK + " ]";
    }

}
