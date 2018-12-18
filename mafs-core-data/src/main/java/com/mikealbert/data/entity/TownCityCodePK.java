package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Composite Key on TownCity
 * @author sibley
 */
@Embeddable
public class TownCityCodePK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @MANotNull(label = "Country")
    @MASize(label = "Country", min = 1, max = 80)
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Basic(optional = false)
    @MANotNull(label = "State")
    @MASize(label = "State", min = 1, max = 80)
    @Column(name = "REGION_CODE")
    private String regionCode;

    @Basic(optional = false)
    @MANotNull(label = "Town")
    @MASize(label = "Town", min = 1, max = 80)
    @Column(name = "TOWN_NAME")
    private String townName;

    @Basic(optional = false)
    @MANotNull(label = "County")
    @MASize(label = "County", min = 1, max = 10)
    @Column(name = "COUNTY_CODE")
    private String countyCode;

    public TownCityCodePK() {
    }

    public TownCityCodePK(String countryCode, String regionCode, String townName, String countyCode) {
        this.countryCode = countryCode;
        this.regionCode = regionCode;
        this.townName = townName;
        this.countyCode = countyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (countryCode != null ? countryCode.hashCode() : 0);
        hash += (regionCode != null ? regionCode.hashCode() : 0);
        hash += (townName != null ? townName.hashCode() : 0);
        hash += (countyCode != null ? countyCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TownCityCodePK)) {
            return false;
        }
        TownCityCodePK other = (TownCityCodePK) object;
        if ((this.countryCode == null && other.countryCode != null) || (this.countryCode != null && !this.countryCode.equals(other.countryCode))) {
            return false;
        }
        if ((this.regionCode == null && other.regionCode != null) || (this.regionCode != null && !this.regionCode.equals(other.regionCode))) {
            return false;
        }
        if ((this.townName == null && other.townName != null) || (this.townName != null && !this.townName.equals(other.townName))) {
            return false;
        }
        if ((this.countyCode == null && other.countyCode != null) || (this.countyCode != null && !this.countyCode.equals(other.countyCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.TownCityCodesPK[ countryCode=" + countryCode + ", regionCode=" + regionCode + ", townName=" + townName + ", countyCode=" + countyCode + " ]";
    }

}
