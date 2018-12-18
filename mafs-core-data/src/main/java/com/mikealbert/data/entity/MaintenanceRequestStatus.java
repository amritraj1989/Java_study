package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;


/**
 * The persistent class for the MAINT_REQUEST_STATUS_CODES database table.
 * @author sibley
 */
@Entity
@Table(name="MAINT_REQUEST_STATUS_CODES")
public class MaintenanceRequestStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Size(min = 1, max = 10)
    @Column(name = "MAINT_REQUEST_STATUS")
    private String code;
    
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Size(min = 1, max = 1)
    @Column(name = "SYSTEM_IND")
    private String systemInd;
    
    @Size(min = 1, max = 10)
    @Column(name = "SORT_ORDER")
    private Integer sortOrder;
    

    public MaintenanceRequestStatus() {}

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

    public String getSystemInd() {
        return systemInd;
    }

    public void setSystemInd(String systemInd) {
        this.systemInd = systemInd;
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
        if (!(object instanceof MaintenanceRequestStatus)) {
            return false;
        }
        MaintenanceRequestStatus other = (MaintenanceRequestStatus) object;
        if ((this.getCode() == null && other.getCode() != null) || (this.getCode() != null && !this.getCode().equals(other.getCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceRequestStatus[ code=" + getCode() + " ]";
    }

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
    	
}