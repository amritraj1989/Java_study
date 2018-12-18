package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Mapped to MAINT_RECHARGE_REASON_CODES Table
 * @author sibley
 */
@Entity
@Table(name = "MAINT_RECHARGE_REASON_CODES")
public class MaintenanceRechargeCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "MAINT_RECHARGE_REASON")
    private String code;
        
    @Size(max=80)
    @Column(name = "MAINT_RECHARGE_DESC")
    private String description;                  
   
    public MaintenanceRechargeCode() {}
    
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

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (getCode() != null ? getCode().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaintenanceRechargeCode)) {
            return false;
        }
        MaintenanceRechargeCode other = (MaintenanceRechargeCode) object;
        if ((this.getCode() == null && other.getCode() != null) || (this.getCode() != null && !this.getCode().equals(other.getCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceRechargeCode[ code=" + getCode() + " ]";
    }
    
}
