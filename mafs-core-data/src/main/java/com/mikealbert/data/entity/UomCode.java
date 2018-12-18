/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to UOM_CODES table
 * @author sibley
 */
@Entity
@Table(name = "UOM_CODES")
public class UomCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "UOM_CODE")
    private String uomCode;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;    
            
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "UOM_TYPE")    
    private String uomTYpe;
    
    @OneToMany(mappedBy = "uomCode")
    private List<MaintenanceCategoryUOM> maintenanceUOMCodes;      
        
    public UomCode() {}

    public UomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public UomCode(String uomCode, String description) {
        this.uomCode = uomCode;
        this.description = description;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getUomTYpe() {
		return uomTYpe;
	}

	public void setUomTYpe(String uomTYpe) {
		this.uomTYpe = uomTYpe;
	}
	
    public List<MaintenanceCategoryUOM> getMaintenanceUOMCodes() {
		return maintenanceUOMCodes;
	}

	public void setMaintenanceUOMCodes(List<MaintenanceCategoryUOM> maintenanceUOMCodes) {
		this.maintenanceUOMCodes = maintenanceUOMCodes;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (uomCode != null ? uomCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UomCode)) {
            return false;
        }
        UomCode other = (UomCode) object;
        if ((this.uomCode == null && other.uomCode != null) || (this.uomCode != null && !this.uomCode.equals(other.uomCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.UomCodes[ uomCode=" + uomCode + " ]";
    }
    
}
