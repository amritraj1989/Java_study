package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to COST_CENTRE_CODES table.
 * @author sibley
 */
@Entity
@Table(name = "COST_CENTRE_CODES")
public class CostCentreCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    protected CostCentreCodePK costCentreCodePK;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "WEB_ALLOW_DRIVER_DETAILS_EDIT")
    private String webAllowDriverDetailsEdit;
        
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID", insertable = false, updatable = false),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE", insertable = false, updatable = false),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE", insertable = false, updatable = false)})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccount;
    
    @OneToMany(mappedBy = "costCentreCodes", fetch = FetchType.LAZY)
    private List<CostCentreCode> costCentreCodesList;
    
    @JoinColumns({
        @JoinColumn(name = "PARENT_CC_CODE", referencedColumnName = "COST_CENTRE_CODE"),
        @JoinColumn(name = "PARENT_C_ID", referencedColumnName = "EA_C_ID"),
        @JoinColumn(name = "PARENT_ACCOUNT_TYPE", referencedColumnName = "EA_ACCOUNT_TYPE"),
        @JoinColumn(name = "PARENT_ACCOUNT_CODE", referencedColumnName = "EA_ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private CostCentreCode costCentreCodes;
    

    public CostCentreCode() {
    }

    public CostCentreCode(CostCentreCodePK costCentreCodesPK) {
        this.costCentreCodePK = costCentreCodesPK;
    }

    public CostCentreCode(CostCentreCodePK costCentreCodesPK, String description, String webAllowDriverDetailsEdit) {
        this.costCentreCodePK = costCentreCodesPK;
        this.description = description;
        this.webAllowDriverDetailsEdit = webAllowDriverDetailsEdit;
    }

    public CostCentreCode(String costCentreCode, long eaCId, String eaAccountType, String eaAccountCode) {
        this.costCentreCodePK = new CostCentreCodePK(costCentreCode, eaCId, eaAccountType, eaAccountCode);
    }

    public CostCentreCodePK getCostCentreCodesPK() {
        return costCentreCodePK;
    }

    public void setCostCentreCodesPK(CostCentreCodePK costCentreCodesPK) {
        this.costCentreCodePK = costCentreCodesPK;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebAllowDriverDetailsEdit() {
        return webAllowDriverDetailsEdit;
    }

    public void setWebAllowDriverDetailsEdit(String webAllowDriverDetailsEdit) {
        this.webAllowDriverDetailsEdit = webAllowDriverDetailsEdit;
    }

    public ExternalAccount getExternalAccount() {
        return externalAccount;
    }

    public void setExternalAccount(ExternalAccount externalAccount) {
        this.externalAccount = externalAccount;
    }

    public List<CostCentreCode> getCostCentreCodesList() {
        return costCentreCodesList;
    }

    public void setCostCentreCodesList(List<CostCentreCode> costCentreCodesList) {
        this.costCentreCodesList = costCentreCodesList;
    }

    public CostCentreCode getCostCentreCodes() {
        return costCentreCodes;
    }

    public void setCostCentreCodes(CostCentreCode costCentreCodes) {
        this.costCentreCodes = costCentreCodes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (costCentreCodePK != null ? costCentreCodePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CostCentreCode)) {
            return false;
        }
        CostCentreCode other = (CostCentreCode) object;
        if ((this.costCentreCodePK == null && other.costCentreCodePK != null) || (this.costCentreCodePK != null && !this.costCentreCodePK.equals(other.costCentreCodePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.CostCentreCode[ costCentreCodesPK=" + costCentreCodePK + " ]";
    }
    
}
