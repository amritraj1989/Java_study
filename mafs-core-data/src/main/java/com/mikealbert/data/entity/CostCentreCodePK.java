package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Composite Primary Key on COST_CENTRE_CODE table
 * @author sibley
 */
@Embeddable
public class CostCentreCodePK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "COST_CENTRE_CODE")
    private String costCentreCode;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "EA_C_ID")
    private long eaCId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "EA_ACCOUNT_TYPE")
    private String eaAccountType;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "EA_ACCOUNT_CODE")
    private String eaAccountCode;

    public CostCentreCodePK() {
    }

    public CostCentreCodePK(String costCentreCode, long eaCId, String eaAccountType, String eaAccountCode) {
        this.costCentreCode = costCentreCode;
        this.eaCId = eaCId;
        this.eaAccountType = eaAccountType;
        this.eaAccountCode = eaAccountCode;
    }

    public String getCostCentreCode() {
        return costCentreCode;
    }

    public void setCostCentreCode(String costCentreCode) {
        this.costCentreCode = costCentreCode;
    }

    public long getEaCId() {
        return eaCId;
    }

    public void setEaCId(long eaCId) {
        this.eaCId = eaCId;
    }

    public String getEaAccountType() {
        return eaAccountType;
    }

    public void setEaAccountType(String eaAccountType) {
        this.eaAccountType = eaAccountType;
    }

    public String getEaAccountCode() {
        return eaAccountCode;
    }

    public void setEaAccountCode(String eaAccountCode) {
        this.eaAccountCode = eaAccountCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (costCentreCode != null ? costCentreCode.hashCode() : 0);
        hash += (int) eaCId;
        hash += (eaAccountType != null ? eaAccountType.hashCode() : 0);
        hash += (eaAccountCode != null ? eaAccountCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CostCentreCodePK)) {
            return false;
        }
        CostCentreCodePK other = (CostCentreCodePK) object;
        if ((this.costCentreCode == null && other.costCentreCode != null) || (this.costCentreCode != null && !this.costCentreCode.equals(other.costCentreCode))) {
            return false;
        }
        if (this.eaCId != other.eaCId) {
            return false;
        }
        if ((this.eaAccountType == null && other.eaAccountType != null) || (this.eaAccountType != null && !this.eaAccountType.equals(other.eaAccountType))) {
            return false;
        }
        if ((this.eaAccountCode == null && other.eaAccountCode != null) || (this.eaAccountCode != null && !this.eaAccountCode.equals(other.eaAccountCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.CostCentreCodesPK[ costCentreCode=" + costCentreCode + ", eaCId=" + eaCId + ", eaAccountType=" + eaAccountType + ", eaAccountCode=" + eaAccountCode + " ]";
    }
    
}
