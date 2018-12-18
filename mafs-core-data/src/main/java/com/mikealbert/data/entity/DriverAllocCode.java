package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to DRIVER_ALLOC_TYPE_CODE table
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_ALLOC_TYPE_CODES")
public class DriverAllocCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ALLOC_TYPE")
    private String allocType;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "TAXABLE_IND")
    private String taxableInd;
        
    public DriverAllocCode() {}

    public DriverAllocCode(String allocType) {
        this.allocType = allocType;
    }

    public DriverAllocCode(String allocType, String description, String taxableInd) {
        this.allocType = allocType;
        this.description = description;
        this.taxableInd = taxableInd;
    }

    public String getAllocType() {
        return allocType;
    }

    public void setAllocType(String allocType) {
        this.allocType = allocType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaxableInd() {
        return taxableInd;
    }

    public void setTaxableInd(String taxableInd) {
        this.taxableInd = taxableInd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (allocType != null ? allocType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverAllocCode)) {
            return false;
        }
        DriverAllocCode other = (DriverAllocCode) object;
        if ((this.allocType == null && other.allocType != null) || (this.allocType != null && !this.allocType.equals(other.allocType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverAllocTypeCodes[ allocType=" + allocType + " ]";
    }
    
}
