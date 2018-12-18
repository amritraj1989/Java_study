package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Mapped to MAINT_REPAIR_REASONS Table
 * @author opitz
 */

@Entity
@Table(name = "MAINT_REPAIR_REASONS")
public class MaintenanceRepairReasons extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @Size(max = 10)
    @Column(name = "MAINT_REP_REASON_CODE")
    private String code;
    
    @Size(max=80)
    @Column(name = "MAINT_REP_REASON_DESC")
    private String description; 
    
    @Column(name = "RECHARGE_ALLOWED_FLAG")
    private String rechargeAllowedFlag;
    
    @Column(name = "POTENTIAL_WARRANTY_FLAG")
    private String potentialWarrantyFlag;
    
    @Column(name = "RATE")
    private Long rate;
    
    @Column(name = "DETAILS_IND")
    private String detailsInd;

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

	public String getRechargeAllowedFlag() {
		return rechargeAllowedFlag;
	}

	public void setRechargeAllowedFlag(String rechargeAllowedFlag) {
		this.rechargeAllowedFlag = rechargeAllowedFlag;
	}

	public String getPotentialWarrantyFlag() {
		return potentialWarrantyFlag;
	}

	public void setPotentialWarrantyFlag(String potentialWarrantyFlag) {
		this.potentialWarrantyFlag = potentialWarrantyFlag;
	}

	public Long getRate() {
		return rate;
	}

	public void setRate(Long rate) {
		this.rate = rate;
	}

	public String getDetailsInd() {
		return detailsInd;
	}

	public void setDetailsInd(String detailsInd) {
		this.detailsInd = detailsInd;
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
        if (!(object instanceof MaintenanceRepairReasons)) {
            return false;
        }
        MaintenanceRepairReasons other = (MaintenanceRepairReasons) object;
        if ((this.getCode() == null && other.getCode() != null) || (this.getCode() != null && !this.getCode().equals(other.getCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceRepairReasons[ code=" + getCode() + " ]";
    }	
}
