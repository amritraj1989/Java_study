package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to DRIVER_GRADES table
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_GRADES")
public class DriverGrade extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "GRADE_CODE")
    private String gradeCode;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "GRADE_DESC")
    private String gradeDesc;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACTIVE_FLAG")
    private String activeFlag;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "CASH_ALTNTV_ALLWD_FLAG")
    private String cashAltntvAllwdFlag;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "CASHBACK_VALUE")
    private BigDecimal cashbackValue;
        
    @OneToMany(mappedBy = "personalBettGradeCode", fetch = FetchType.LAZY)
    private List<Driver> driversList;
    
    @OneToMany(mappedBy = "dgdGradeCode", fetch = FetchType.LAZY)
    private List<Driver> driversList1;
    
    @JoinColumn(name = "GRADE_GROUP", referencedColumnName = "DRIVER_GRADE_GROUP")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private DriverGradeGroupCode gradeGroup;

    public DriverGrade() {
    }

    public DriverGrade(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    public DriverGrade(String gradeCode, String gradeDesc, String activeFlag, String cashAltntvAllwdFlag) {
        this.gradeCode = gradeCode;
        this.gradeDesc = gradeDesc;
        this.activeFlag = activeFlag;
        this.cashAltntvAllwdFlag = cashAltntvAllwdFlag;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    public String getGradeDesc() {
        return gradeDesc;
    }

    public void setGradeDesc(String gradeDesc) {
        this.gradeDesc = gradeDesc;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getCashAltntvAllwdFlag() {
        return cashAltntvAllwdFlag;
    }

    public void setCashAltntvAllwdFlag(String cashAltntvAllwdFlag) {
        this.cashAltntvAllwdFlag = cashAltntvAllwdFlag;
    }

    public BigDecimal getCashbackValue() {
        return cashbackValue;
    }

    public void setCashbackValue(BigDecimal cashbackValue) {
        this.cashbackValue = cashbackValue;
    }

    public List<Driver> getDriversList() {
        return driversList;
    }

    public void setDriversList(List<Driver> driversList) {
        this.driversList = driversList;
    }

    public List<Driver> getDriversList1() {
        return driversList1;
    }

    public void setDriversList1(List<Driver> driversList1) {
        this.driversList1 = driversList1;
    }

    public DriverGradeGroupCode getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(DriverGradeGroupCode gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gradeCode != null ? gradeCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverGrade)) {
            return false;
        }
        DriverGrade other = (DriverGrade) object;
        if ((this.gradeCode == null && other.gradeCode != null) || (this.gradeCode != null && !this.gradeCode.equals(other.gradeCode))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverGrades[ gradeCode=" + gradeCode + " ]";
    }
    
}
