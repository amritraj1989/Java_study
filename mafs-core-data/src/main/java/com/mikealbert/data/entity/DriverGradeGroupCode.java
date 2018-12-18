package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to DRIVER_GRADE_GROUP_CODES table
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_GRADE_GROUP_CODES")
public class DriverGradeGroupCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "DRIVER_GRADE_GROUP")
    private String driverGradeGroup;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
        
    @OneToMany(mappedBy = "gradeGroup", fetch = FetchType.LAZY)
    private List<DriverGrade> driverGradesList;
    
    @OneToMany(mappedBy = "driverGradeGroup", fetch = FetchType.LAZY)
    private List<ExternalAccountGradeGroup> externalAccountGradeGroupsList;

    public DriverGradeGroupCode() {
    }

    public DriverGradeGroupCode(String driverGradeGroup) {
        this.driverGradeGroup = driverGradeGroup;
    }

    public DriverGradeGroupCode(String driverGradeGroup, String description) {
        this.driverGradeGroup = driverGradeGroup;
        this.description = description;
    }

    public String getDriverGradeGroup() {
        return driverGradeGroup;
    }

    public void setDriverGradeGroup(String driverGradeGroup) {
        this.driverGradeGroup = driverGradeGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DriverGrade> getDriverGradesList() {
        return driverGradesList;
    }

    public void setDriverGradesList(List<DriverGrade> driverGradesList) {
        this.driverGradesList = driverGradesList;
    }

    public List<ExternalAccountGradeGroup> getExternalAccountGradeGroupsList() {
        return externalAccountGradeGroupsList;
    }

    public void setExternalAccountGradeGroupsList(List<ExternalAccountGradeGroup> externalAccountGradeGroupsList) {
        this.externalAccountGradeGroupsList = externalAccountGradeGroupsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (driverGradeGroup != null ? driverGradeGroup.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverGradeGroupCode)) {
            return false;
        }
        DriverGradeGroupCode other = (DriverGradeGroupCode) object;
        if ((this.driverGradeGroup == null && other.driverGradeGroup != null) || (this.driverGradeGroup != null && !this.driverGradeGroup.equals(other.driverGradeGroup))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverGradeGroupCodes[ driverGradeGroup=" + driverGradeGroup + " ]";
    }
    
}
