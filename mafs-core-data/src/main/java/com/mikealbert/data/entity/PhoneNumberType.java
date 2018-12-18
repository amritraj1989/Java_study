package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 *
 * @author sibley
 */
@Entity
@Table(name = "CONTACT_NUMBER_TYPE_CODES")
public class PhoneNumberType extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "NUMBER_TYPE")
    private String code;
    
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @NotNull
    @Size(max = 1)
    @Column(name = "ASSIGN_DRIVERS")
    private String assignDrivers;
        
    public PhoneNumberType() {
    }

    public PhoneNumberType(String numberType) {
        this.code = numberType;
    }

    public PhoneNumberType(String numberType, String description) {
        this.code = numberType;
        this.description = description;
    }

    public String getNumberType() {
        return code;
    }

    public void setNumberType(String numberType) {
        this.code = numberType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        hash += (code != null ? code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PhoneNumberType)) {
            return false;
        }
        PhoneNumberType other = (PhoneNumberType) object;
        if ((this.code == null && other.code != null) || (this.code != null && !this.code.equals(other.code))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.PhoneNumberType[ code=" + code + " ]";
    }
    
}
