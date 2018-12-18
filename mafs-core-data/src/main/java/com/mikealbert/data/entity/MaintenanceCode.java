package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to MAINTENANCE_CODES Table
 * @author sibley
 */
@Entity
@Table(name = "MAINTENANCE_CODES")
public class MaintenanceCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "MCO_ID")
    private Long mcoId;
    
    @MASize(label = "Maintenance Code", min = 1, max = 10)
    @Column(name = "MAINT_CODE")
    private String code;
    
    @MASize(label = "Maintenance Code Description", min = 1, max = 80)
    @Column(name = "MAINT_CODE_DESC")
    private String description;        
    
    @MASize(label = "Maintenance Code Category", min = 1, max = 80)
    @Column(name = "MAINT_CAT_CODE")
    private String maintCatCode;
    
    @MASize(label = "Maintenance Code Type", min = 1, max = 1)
    @Column(name = "CODE_TYPE")
    private String maintCodeType;
    
    @Column(name = "OIL_CHANGE")
    private String oilChange; 
   
    public String getMaintCodeType() {
		return maintCodeType;
	}


	public void setMaintCodeType(String maintCodeType) {
		this.maintCodeType = maintCodeType;
	}


	public String getMaintCatCode() {
		return maintCatCode;
	}


	public void setMaintCatCode(String maintCatCode) {
		this.maintCatCode = maintCatCode;
	}


	public MaintenanceCode() {}
    

	public Long getMcoId() {
		return mcoId;
	}

	public void setMcoId(Long mcoId) {
		this.mcoId = mcoId;
	}

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

    public String getOilChange() {
		return oilChange;
	}


	public void setOilChange(String oilChange) {
		this.oilChange = oilChange;
	}


	public MaintenanceCode(MaintenanceCode maintenanceCode) {
    	this.mcoId = maintenanceCode.getMcoId();
    	this.code = maintenanceCode.getCode();
    	this.description = maintenanceCode.getDescription();
    	this.maintCatCode = maintenanceCode.getMaintCatCode();
    	// This is an exception to the rule; almost always we don't want to set a VersionTS; but in a copy constructor
    	// we absolutely must so that Hibernate honors the copy as a true deep copy.
    	this.versionts = maintenanceCode.getVersionts();
    }
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (getMcoId() != null ? getMcoId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaintenanceCode)) {
            return false;
        }
        MaintenanceCode other = (MaintenanceCode) object;
        if ((this.getMcoId() == null && other.getMcoId() != null) || (this.getMcoId() != null && !this.getMcoId().equals(other.getMcoId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceCode[ mcoId=" + getMcoId() + " ]";
    }
    
}
