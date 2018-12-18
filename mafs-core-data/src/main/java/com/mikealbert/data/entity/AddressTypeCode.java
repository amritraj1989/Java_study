package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to ADDRESS_TYPE_CODES table
 * @author sibley
 */
@Entity
@Table(name = "ADDRESS_TYPE_CODES")
public class AddressTypeCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @MANotNull(label = "Address Type")
    @MASize(label = "Address Type", min = 1, max = 10)
    @Column(name = "ADDRESS_TYPE")
    private String addressType;

    @Basic(optional = false)
    @MANotNull(label = "Description")
    @MASize(label = "Description", min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;

    @Size(max = 1)
    @Column(name = "ASSIGN_DRIVERS")
    private String assignDrivers;
    
    @OneToMany(mappedBy = "addressType")
    private Collection<DriverAddressHistory> driverAddressesHistoryCollection;

    @OneToMany(mappedBy = "addressType")
    private Collection<DriverAddress> driverAddressesCollection;

    public AddressTypeCode() {
    }

    public AddressTypeCode(String addressType) {
        this.addressType = addressType;
    }

    public AddressTypeCode(String addressType, String description) {
        this.addressType = addressType;
        this.description = description;
    }
    
    public AddressTypeCode(String addressType, String description, String assignDrivers) {
        this.addressType = addressType;
        this.description = description;
        this.assignDrivers = assignDrivers;
    }
    
    
    // This is a simple shallow copy constructor (a simpler and cleaner/safer approach than overloading the clone method
    public AddressTypeCode(AddressTypeCode origAddressTypeCode) {
        this(origAddressTypeCode.addressType,origAddressTypeCode.description,origAddressTypeCode.assignDrivers);
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<DriverAddressHistory> getDriverAddressesHistoryCollection() {
        return driverAddressesHistoryCollection;
    }

    public void setDriverAddressesHistoryCollection(Collection<DriverAddressHistory> driverAddressesHistoryCollection) {
        this.driverAddressesHistoryCollection = driverAddressesHistoryCollection;
    }

    public Collection<DriverAddress> getDriverAddressesCollection() {
        return driverAddressesCollection;
    }

    public void setDriverAddressesCollection(Collection<DriverAddress> driverAddressesCollection) {
        this.driverAddressesCollection = driverAddressesCollection;
    }

	public String getAssignDrivers() {
		return assignDrivers;
	}

	public void setAssignDrivers(String assignDrivers) {
		this.assignDrivers = assignDrivers;
	}
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (addressType != null ? addressType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AddressTypeCode)) {
            return false;
        }
        AddressTypeCode other = (AddressTypeCode) object;
        if ((this.addressType == null && other.addressType != null) || (this.addressType != null && !this.addressType.equals(other.addressType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.AddressTypeCodes[ addressType=" + addressType + " ]";
    }

}
