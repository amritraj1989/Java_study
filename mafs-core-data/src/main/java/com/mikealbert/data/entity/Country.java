package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to COUNTRY_CODES table
 * @author sibley
 */
@Entity
@Table(name = "COUNTRY_CODES")
public class Country extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Size(max = 240)
    @Column(name = "COUNTRY_DESC")
    private String countryDesc;

    @Size(max = 1)
    @Column(name = "DEFAULT_IND")
    private String defaultInd;

    @OneToMany(mappedBy = "country")
    private Collection<RegionCode> regionCodesCollection;

    public Country() {
    }

    public Country(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryDesc() {
        return countryDesc;
    }

    public void setCountryDesc(String countryDesc) {
        this.countryDesc = countryDesc;
    }

    public String getDefaultInd() {
        return defaultInd;
    }

    public void setDefaultInd(String defaultInd) {
        this.defaultInd = defaultInd;
    }

    public Collection<RegionCode> getRegionCodesCollection() {
        return regionCodesCollection;
    }

    public void setRegionCodesCollection(Collection<RegionCode> regionCodesCollection) {
        this.regionCodesCollection = regionCodesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (countryCode != null ? countryCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Country)) {
            return false;
        }
        Country other = (Country) object;
        if ((this.countryCode == null && other.countryCode != null) || (this.countryCode != null && !this.countryCode.equals(other.countryCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.CountryCodes[ countryCode=" + countryCode + " ]";
    }

}
