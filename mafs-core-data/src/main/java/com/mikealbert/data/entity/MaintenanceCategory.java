package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Mapped to MAINTENANCE_CATEGORIES Table
 * @author sibley
 */
@Entity
@Table(name = "MAINTENANCE_CATEGORIES")
public class MaintenanceCategory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Size(max = 10)
    @Column(name = "MAINT_CAT_CODE")
    private String code;
        
    @Size(max=80)
    @Column(name = "MAINT_CAT_DESC")
    private String description;                  
   
    @OneToMany(mappedBy = "maintenanceCategory")
    private List<MaintenanceCategoryProperty> maintenanceCategoryProperties; 
    
    @OneToMany(mappedBy = "maintenanceCategory")
    private List<MaintenanceCategoryUOM> maintenanceUOMCodes;     
    
    public MaintenanceCategory() {}
    

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MaintenanceCategoryProperty> getMaintenanceCategoryProperties() {
		return maintenanceCategoryProperties;
	}


	public void setMaintenanceCategoryProperties(
			List<MaintenanceCategoryProperty> maintenanceCategoryProperties) {
		this.maintenanceCategoryProperties = maintenanceCategoryProperties;
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
        hash += (getCode() != null ? getCode().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaintenanceCategory)) {
            return false;
        }
        MaintenanceCategory other = (MaintenanceCategory) object;
        if ((this.getCode() == null && other.getCode() != null) || (this.getCode() != null && !this.getCode().equals(other.getCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceCategory[ code=" + getCode() + " ]";
    }
    
}
