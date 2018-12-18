package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to REGION_CODES table
 * @author sibley
 */
@Entity
@Table(name = "REGION_CODES")
public class RegionCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected RegionCodePK regionCodesPK;

    @Size(max = 240)
    @Column(name = "REGION_DESC")
    private String regionDesc;

    @Size(max = 10)
    @Column(name = "SHORT_CODE")
    private String shortCode;

    @Size(max = 80)
    @Column(name = "ADDRESS_NAME_1")
    private String addressName1;

    @Size(max = 80)
    @Column(name = "ADDRESS_NAME_2")
    private String addressName2;

    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_1")
    private String addressLine1;

    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_2")
    private String addressLine2;

    @Size(max = 2048)
    @Column(name = "COMMENTS")
    private String comments;

    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 80)
    @Column(name = "EMAIL")
    private String email;

    @Size(max = 20)
    @Column(name = "FAX_NO")
    private String faxNo;

    @Size(max = 20)
    @Column(name = "TEL_NO")
    private String telNo;

    @Size(max = 10)
    @Column(name = "ZIP_CODE")
    private String zipCode;

    @OneToMany(mappedBy = "regionCode")
    private Collection<TownCityCode> townCityCodesCollection;

    @OneToMany(mappedBy = "regionCode")
    private Collection<CountyCode> countyCodesCollection;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Country country;

    public RegionCode() {
    }

    public RegionCode(RegionCodePK regionCodesPK) {
        this.regionCodesPK = regionCodesPK;
    }

    public RegionCode(String countryCode, String regionCode) {
        this.regionCodesPK = new RegionCodePK(countryCode, regionCode);
    }

    public RegionCodePK getRegionCodesPK() {
        return regionCodesPK;
    }

    public void setRegionCodesPK(RegionCodePK regionCodesPK) {
        this.regionCodesPK = regionCodesPK;
    }

    public String getRegionDesc() {
        return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
        this.regionDesc = regionDesc;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getAddressName1() {
        return addressName1;
    }

    public void setAddressName1(String addressName1) {
        this.addressName1 = addressName1;
    }

    public String getAddressName2() {
        return addressName2;
    }

    public void setAddressName2(String addressName2) {
        this.addressName2 = addressName2;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Collection<TownCityCode> getTownCityCodesCollection() {
        return townCityCodesCollection;
    }

    public void setTownCityCodesCollection(Collection<TownCityCode> townCityCodesCollection) {
        this.townCityCodesCollection = townCityCodesCollection;
    }

    public Collection<CountyCode> getCountyCodesCollection() {
        return countyCodesCollection;
    }

    public void setCountyCodesCollection(Collection<CountyCode> countyCodesCollection) {
        this.countyCodesCollection = countyCodesCollection;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (regionCodesPK != null ? regionCodesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegionCode)) {
            return false;
        }
        RegionCode other = (RegionCode) object;
        if ((this.regionCodesPK == null && other.regionCodesPK != null) || (this.regionCodesPK != null && !this.regionCodesPK.equals(other.regionCodesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.RegionCodes[ regionCodesPK=" + regionCodesPK + " ]";
    }

}
