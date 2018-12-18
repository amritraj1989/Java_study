package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Composite PK on ContyCode
 * @author sibley
 */
@Embeddable
public class CountyCodePK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "REGION_CODE")
    private String regionCode;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "COUNTY_CODE")
    private String countyCode;

    public CountyCodePK() {
    }

    public CountyCodePK(String countryCode, String regionCode, String countyCode) {
        this.countryCode = countryCode;
        this.regionCode = regionCode;
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
        hash += (countyCode != null ? countyCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CountyCodePK)) {
            return false;
        }
        CountyCodePK other = (CountyCodePK) object;
        if ((this.countryCode == null && other.countryCode != null) || (this.countryCode != null && !this.countryCode.equals(other.countryCode))) {
            return false;
        }
        if ((this.regionCode == null && other.regionCode != null) || (this.regionCode != null && !this.regionCode.equals(other.regionCode))) {
            return false;
        }
        if ((this.countyCode == null && other.countyCode != null) || (this.countyCode != null && !this.countyCode.equals(other.countyCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.CountyCodesPK[ countryCode=" + countryCode + ", regionCode=" + regionCode + ", countyCode=" + countyCode + " ]";
    }

}
