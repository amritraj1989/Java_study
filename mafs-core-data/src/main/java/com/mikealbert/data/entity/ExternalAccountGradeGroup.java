package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mapped to the EXTERNAL_ACCOUNT_GRADE_GROUPS table
 * @author sibley
 */
@Entity
@Table(name = "EXTERNAL_ACCOUNT_GRADE_GROUPS")
public class ExternalAccountGradeGroup extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="EAG_SEQ", sequenceName="EAG_SEQ", allocationSize=1)        
    @Basic(optional = false)
    @NotNull
    @Column(name = "EAG_ID")
    private Long eagId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "COMB_ACC_IND")
    private String combAccInd;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "COMB_ACC_LIMIT")
    private BigDecimal combAccLimit;
    
    @Column(name = "UNSCD_REPAIR_LIMIT")
    private BigDecimal unscdRepairLimit;
    
    @Column(name = "MAINT_AUTH_LIMIT")
    private BigDecimal maintAuthLimit;
        
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts;
    
    @JoinColumn(name = "DRIVER_GRADE_GROUP", referencedColumnName = "DRIVER_GRADE_GROUP")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private DriverGradeGroupCode driverGradeGroup;

    public ExternalAccountGradeGroup() {
    }

    public ExternalAccountGradeGroup(Long eagId) {
        this.eagId = eagId;
    }

    public ExternalAccountGradeGroup(Long eagId, String combAccInd) {
        this.eagId = eagId;
        this.combAccInd = combAccInd;
    }

    public Long getEagId() {
        return eagId;
    }

    public void setEagId(Long eagId) {
        this.eagId = eagId;
    }

    public String getCombAccInd() {
        return combAccInd;
    }

    public void setCombAccInd(String combAccInd) {
        this.combAccInd = combAccInd;
    }

    public BigDecimal getCombAccLimit() {
        return combAccLimit;
    }

    public void setCombAccLimit(BigDecimal combAccLimit) {
        this.combAccLimit = combAccLimit;
    }

    public BigDecimal getUnscdRepairLimit() {
        return unscdRepairLimit;
    }

    public void setUnscdRepairLimit(BigDecimal unscdRepairLimit) {
        this.unscdRepairLimit = unscdRepairLimit;
    }

    public BigDecimal getMaintAuthLimit() {
        return maintAuthLimit;
    }

    public void setMaintAuthLimit(BigDecimal maintAuthLimit) {
        this.maintAuthLimit = maintAuthLimit;
    }

    public ExternalAccount getExternalAccounts() {
        return externalAccounts;
    }

    public void setExternalAccounts(ExternalAccount externalAccounts) {
        this.externalAccounts = externalAccounts;
    }

    public DriverGradeGroupCode getDriverGradeGroup() {
        return driverGradeGroup;
    }

    public void setDriverGradeGroup(DriverGradeGroupCode driverGradeGroup) {
        this.driverGradeGroup = driverGradeGroup;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eagId != null ? eagId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExternalAccountGradeGroup)) {
            return false;
        }
        ExternalAccountGradeGroup other = (ExternalAccountGradeGroup) object;
        if ((this.eagId == null && other.eagId != null) || (this.eagId != null && !this.eagId.equals(other.eagId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ExternalAccountGradeGroups[ eagId=" + eagId + " ]";
    }
    
}
