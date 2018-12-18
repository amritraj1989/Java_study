package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MADriverAddresses;
import com.mikealbert.data.beanvalidation.MAEmail;
import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to DRIVERS Table
 * @author sibley
 */
@Entity
@Table(name = "DRIVERS")
public class Driver extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
   
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DRV_SEQ")    
    @SequenceGenerator(name="DRV_SEQ", sequenceName="DRV_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "DRV_ID")
    private Long drvId;

    @Basic(optional = false)    
    @MASize(label = "Driver First Name", min = 1, max = 30)
    @Column(name = "DRIVER_FORENAME")
    private String driverForename;

    @Basic(optional = false)    
    @MASize(label = "Driver Last Name", min = 1, max = 40)
    @Column(name = "DRIVER_SURNAME")
    private String driverSurname;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "NAT_INS_NO")
    private String natInsNo = "N/A";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "BG_SALARY_UPLIFT")
    private String bgSalaryUplift = "N";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "FULL_NAT_LICENCE_FLAG")
    private String fullNatLicenceFlag = "N";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "NEW_CAR_AUTHD_FLAG")
    private String newCarAuthdFlag = "N";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "CASH_ALTERNATIVE_FLAG")
    private String cashAlternativeFlag = "N";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "VEHICLE_ON_RETENTION_FLAG")
    private String vehicleOnRetentionFlag = "N";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PUC_COLLECTION_IND")
    private String pucCollectionInd = "N";

    @Size(max = 30)
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @Size(max = 30)
    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "DATE_JOINED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateJoined;

    @Column(name = "DATE_RESIGNED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateResigned;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "SALARY")
    private BigDecimal salary;

    @Column(name = "DATE_SALARY_CHANGED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSalaryChanged;

    @Column(name = "DRIVER_ALLOWANCE")
    private BigDecimal driverAllowance;

    @Column(name = "RENTAL_ALLOWANCE")
    private BigDecimal rentalAllowance;

    @Size(max = 25)
    @Column(name = "DRIVING_LICENCE_NO")
    private String drivingLicenceNo;

    @Column(name = "DRIVING_LICENCE_EXPIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date drivingLicenceExpiryDate;

    @Column(name = "ESTIMATED_ANNUAL_MILEAGE")
    private Integer estimatedAnnualMileage;

    @Column(name = "NO_OF_ENDORSEMENT_POINTS")
    private Short noOfEndorsementPoints;

    @Size(max = 10)
    @Column(name = "PERSONAL_BETT_GRADE_GROUP_CODE")
    private String personalBettGradeGroupCode;

    @Size(max = 6)
    @Column(name = "VEHICLE_LIST_NO")
    private String vehicleListNo;

    @Size(max = 25)
    @Column(name = "RECHARGE_CODE")
    private String rechargeCode;

    @Column(name = "PUC_BASIC")
    private BigDecimal pucBasic;

    @Column(name = "PUC_EXTRAS_ELEMENT")
    private BigDecimal pucExtrasElement;

    @Column(name = "PUC_TRADE_UP_ELEMENT")
    private BigDecimal pucTradeUpElement;

    @Column(name = "PUC_TRADE_DOWN_ELEMENT")
    private BigDecimal pucTradeDownElement;

    @Column(name = "PUC_TOTAL_AMOUNT")
    private BigDecimal pucTotalAmount;

    @Column(name = "PUC_CHANGED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pucChangedDate;

    @Size(max = 25)
    @Column(name = "PAYROLL_NO")
    private String payrollNo;

    @Size(max = 15)
    @Column(name = "DRIVER_HANDBOOK_VERSION_NO")
    private String driverHandbookVersionNo;

    @Size(max = 15)
    @Column(name = "DRIVER_HANDBOOK_LIST_NO")
    private String driverHandbookListNo;

    @Size(max = 15)
    @Column(name = "DRIVER_HANDBOOK_STOP_PRESS_NO")
    private String driverHandbookStopPressNo;

    @Size(max = 240)
    @Column(name = "DISSABILITIES")
    private String dissabilities;

    @Size(max = 10)
    @Column(name = "DRV_INS_CAT_CODE")
    private String drvInsCatCode;

    @MAEmail
    @Size(max = 80)
    @Column(name = "EMAIL")
    private String email;
    
    @Size(max = 1)
    @Column(name = "POOL_MGR")
    private String poolManager;

    @Size(max = 80)
    @Column(name = "DRIVER_MIDDLENAME")
    private String driverMiddlename;
    
    @Column(name = "COCC_C_ID")
    private Long costCentreAccountCorporateId;
    
    @Column(name = "COCC_ACCOUNT_CODE")
    private String costCentreAccountCode;
    
    @Column(name = "COCC_ACCOUNT_TYPE")
    private String costCentreAccountType; 

    @Basic(optional = false)       
    @MASize(label = "Title", min = 1, max = 10)   
    @Column(name = "TITLE")    
    private String title;

    @Basic(optional = false)    
    @MASize(label = "Manual Status", min = 1, max = 10)    
    @Column(name = "MANUAL_STATUS")     
    private String manualStatus;
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;

    @JoinColumns({
        @JoinColumn(name = "EA_C_ID_HAS_ACCOUNT", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE_HAS_ACCOUNT", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE_HAS_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount externalAccount1;

    @JoinColumn(name = "PERSONAL_BETT_GRADE_CODE", referencedColumnName = "GRADE_CODE")
    @ManyToOne(fetch = FetchType.LAZY)
    private DriverGrade personalBettGradeCode;

    @JoinColumn(name = "DGD_GRADE_CODE", referencedColumnName = "GRADE_CODE")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private DriverGrade dgdGradeCode;

    @OneToMany(mappedBy = "drvDrvId", fetch = FetchType.LAZY)
    private List<Driver> driversList;

    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Driver drvDrvId;

    @Deprecated
    @Size(max = 60)
    @Column(name = "DRIVER_COST_CENTRE")
    private String costCentreCode;

    @MADriverAddresses(label="Driver Addresses")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "driver", fetch = FetchType.EAGER)
    private List<DriverAddress> driverAddressList;
    
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<DriverAllocation> driverAllocationList;    

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<DriverAddressHistory> driverAddressHistoryList;
       
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "driver", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PhoneNumber> phoneNumbers;  
    

    @OneToMany(mappedBy = "primaryDriver")
    private List<DriverRelationship> childRelationships;
    
    @OneToMany(mappedBy = "secondaryDriver")
    private List<DriverRelationship> parentRelationships;    

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "driver", fetch = FetchType.LAZY)
    private List<DriverCostCenter> driverCostCenterList;
    
    
    
    @MANotNull (label = "Driver Active Indicator")
    @Column(name = "ACTIVE_IND")
    private String activeInd;
   

	public Driver() {}

    public Driver(Long drvId) {
        this.drvId = drvId;
    }

    public Driver(Long drvId, ExternalAccount externalAccount, DriverGrade dgdGradeCode, String driverForename, String driverSurname) {
        this.drvId = drvId;
        this.externalAccount = externalAccount;
        this.dgdGradeCode = dgdGradeCode;
        this.driverForename = driverForename;
        this.driverSurname = driverSurname;
    }

    public Long getDrvId() {
        return drvId;
    }

    public void setDrvId(Long drvId) {
        this.drvId = drvId;
    }

    public String getDriverForename() {
        return driverForename;
    }

    public void setDriverForename(String driverForename) {
        this.driverForename = driverForename;
    }

    public String getDriverSurname() {
        return driverSurname;
    }

    public void setDriverSurname(String driverSurname) {
        this.driverSurname = driverSurname;
    }

    public String getNatInsNo() {
        return natInsNo;
    }

    public void setNatInsNo(String natInsNo) {
        this.natInsNo = natInsNo;
    }

    public String getBgSalaryUplift() {
        return bgSalaryUplift;
    }

    public void setBgSalaryUplift(String bgSalaryUplift) {
        this.bgSalaryUplift = bgSalaryUplift;
    }

    public String getFullNatLicenceFlag() {
        return fullNatLicenceFlag;
    }

    public void setFullNatLicenceFlag(String fullNatLicenceFlag) {
        this.fullNatLicenceFlag = fullNatLicenceFlag;
    }

    public String getNewCarAuthdFlag() {
        return newCarAuthdFlag;
    }

    public void setNewCarAuthdFlag(String newCarAuthdFlag) {
        this.newCarAuthdFlag = newCarAuthdFlag;
    }

    public String getCashAlternativeFlag() {
        return cashAlternativeFlag;
    }

    public void setCashAlternativeFlag(String cashAlternativeFlag) {
        this.cashAlternativeFlag = cashAlternativeFlag;
    }

    public String getVehicleOnRetentionFlag() {
        return vehicleOnRetentionFlag;
    }

    public void setVehicleOnRetentionFlag(String vehicleOnRetentionFlag) {
        this.vehicleOnRetentionFlag = vehicleOnRetentionFlag;
    }

    public String getPucCollectionInd() {
        return pucCollectionInd;
    }

    public void setPucCollectionInd(String pucCollectionInd) {
        this.pucCollectionInd = pucCollectionInd;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public Date getDateResigned() {
        return dateResigned;
    }

    public void setDateResigned(Date dateResigned) {
        this.dateResigned = dateResigned;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Date getDateSalaryChanged() {
        return dateSalaryChanged;
    }

    public void setDateSalaryChanged(Date dateSalaryChanged) {
        this.dateSalaryChanged = dateSalaryChanged;
    }

    public BigDecimal getDriverAllowance() {
        return driverAllowance;
    }

    public void setDriverAllowance(BigDecimal driverAllowance) {
        this.driverAllowance = driverAllowance;
    }

    public BigDecimal getRentalAllowance() {
        return rentalAllowance;
    }

    public void setRentalAllowance(BigDecimal rentalAllowance) {
        this.rentalAllowance = rentalAllowance;
    }

    public String getDrivingLicenceNo() {
        return drivingLicenceNo;
    }

    public void setDrivingLicenceNo(String drivingLicenceNo) {
        this.drivingLicenceNo = drivingLicenceNo;
    }

    public Date getDrivingLicenceExpiryDate() {
        return drivingLicenceExpiryDate;
    }

    public void setDrivingLicenceExpiryDate(Date drivingLicenceExpiryDate) {
        this.drivingLicenceExpiryDate = drivingLicenceExpiryDate;
    }

    public Integer getEstimatedAnnualMileage() {
        return estimatedAnnualMileage;
    }

    public void setEstimatedAnnualMileage(Integer estimatedAnnualMileage) {
        this.estimatedAnnualMileage = estimatedAnnualMileage;
    }

    public Short getNoOfEndorsementPoints() {
        return noOfEndorsementPoints;
    }

    public void setNoOfEndorsementPoints(Short noOfEndorsementPoints) {
        this.noOfEndorsementPoints = noOfEndorsementPoints;
    }

    public String getPersonalBettGradeGroupCode() {
        return personalBettGradeGroupCode;
    }

    public void setPersonalBettGradeGroupCode(String personalBettGradeGroupCode) {
        this.personalBettGradeGroupCode = personalBettGradeGroupCode;
    }

    public String getVehicleListNo() {
        return vehicleListNo;
    }

    public void setVehicleListNo(String vehicleListNo) {
        this.vehicleListNo = vehicleListNo;
    }

    public String getRechargeCode() {
        return rechargeCode;
    }

    public void setRechargeCode(String rechargeCode) {
        this.rechargeCode = rechargeCode;
    }

    public BigDecimal getPucBasic() {
        return pucBasic;
    }

    public void setPucBasic(BigDecimal pucBasic) {
        this.pucBasic = pucBasic;
    }

    public BigDecimal getPucExtrasElement() {
        return pucExtrasElement;
    }

    public void setPucExtrasElement(BigDecimal pucExtrasElement) {
        this.pucExtrasElement = pucExtrasElement;
    }

    public BigDecimal getPucTradeUpElement() {
        return pucTradeUpElement;
    }

    public void setPucTradeUpElement(BigDecimal pucTradeUpElement) {
        this.pucTradeUpElement = pucTradeUpElement;
    }

    public BigDecimal getPucTradeDownElement() {
        return pucTradeDownElement;
    }

    public void setPucTradeDownElement(BigDecimal pucTradeDownElement) {
        this.pucTradeDownElement = pucTradeDownElement;
    }

    public BigDecimal getPucTotalAmount() {
        return pucTotalAmount;
    }

    public void setPucTotalAmount(BigDecimal pucTotalAmount) {
        this.pucTotalAmount = pucTotalAmount;
    }

    public Date getPucChangedDate() {
        return pucChangedDate;
    }

    public void setPucChangedDate(Date pucChangedDate) {
        this.pucChangedDate = pucChangedDate;
    }

    public String getPayrollNo() {
        return payrollNo;
    }

    public void setPayrollNo(String payrollNo) {
        this.payrollNo = payrollNo;
    }

    public String getDriverHandbookVersionNo() {
        return driverHandbookVersionNo;
    }

    public void setDriverHandbookVersionNo(String driverHandbookVersionNo) {
        this.driverHandbookVersionNo = driverHandbookVersionNo;
    }

    public String getDriverHandbookListNo() {
        return driverHandbookListNo;
    }

    public void setDriverHandbookListNo(String driverHandbookListNo) {
        this.driverHandbookListNo = driverHandbookListNo;
    }

    public String getDriverHandbookStopPressNo() {
        return driverHandbookStopPressNo;
    }

    public void setDriverHandbookStopPressNo(String driverHandbookStopPressNo) {
        this.driverHandbookStopPressNo = driverHandbookStopPressNo;
    }

    public String getDissabilities() {
        return dissabilities;
    }

    public void setDissabilities(String dissabilities) {
        this.dissabilities = dissabilities;
    }

    public String getDrvInsCatCode() {
        return drvInsCatCode;
    }

    public void setDrvInsCatCode(String drvInsCatCode) {
        this.drvInsCatCode = drvInsCatCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDriverMiddlename() {
        return driverMiddlename;
    }

    public void setDriverMiddlename(String driverMiddlename) {
        this.driverMiddlename = driverMiddlename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExternalAccount getExternalAccount() {
        return externalAccount;
    }

    public void setExternalAccount(ExternalAccount externalAccount) {
        this.externalAccount = externalAccount;
    }

    public ExternalAccount getExternalAccount1() {
        return externalAccount1;
    }

    public void setExternalAccount1(ExternalAccount externalAccount1) {
        this.externalAccount1 = externalAccount1;
    }

    public String getManualStatus() {
        return manualStatus;
    }

    public void setManualStatus(String manualStatus) {
        this.manualStatus = manualStatus;
    }
    
    public DriverGrade getPersonalBettGradeCode() {
        return personalBettGradeCode;
    }

    public void setPersonalBettGradeCode(DriverGrade personalBettGradeCode) {
        this.personalBettGradeCode = personalBettGradeCode;
    }

    public DriverGrade getDgdGradeCode() {
        return dgdGradeCode;
    }

    public void setDgdGradeCode(DriverGrade dgdGradeCode) {
        this.dgdGradeCode = dgdGradeCode;
    }

    public List<Driver> getDriversList() {
        return driversList;
    }

    public void setDriversList(List<Driver> driversList) {
        this.driversList = driversList;
    }

    public Driver getDrvDrvId() {
        return drvDrvId;
    }

    public void setDrvDrvId(Driver drvDrvId) {
        this.drvDrvId = drvDrvId;
    }
    
    @Deprecated
    /**
     * The Cost Centre column directly off of the driver entity/table should be retired as soon as possible; we need to keep it
     * around to do needs by other systems that will not have the bandwidth to change right now.
     */
    public String getCostCentreCode() {
        return costCentreCode;
    }

    @Deprecated
    /**
     * The Cost Centre column directly off of the driver entity/table should be retired as soon as possible; we need to keep it
     * around to do needs by other systems that will not have the bandwidth to change right now.
     */
    public void setCostCentreCode(String costCentreCode) {
        this.costCentreCode = costCentreCode;
    }


    public List<DriverAddress> getDriverAddressList() {
        return driverAddressList;
    }

    public void setDriverAddressList(List<DriverAddress> driverAddressList) {
        this.driverAddressList = driverAddressList;
    }

    public List<DriverAddressHistory> getDriverAddressHistoryList() {
        return driverAddressHistoryList;
    }

    public void setDriverAddressHistoryList(List<DriverAddressHistory> driverAddressHistoryList) {
        this.driverAddressHistoryList = driverAddressHistoryList;
    }

    public List<DriverAllocation> getDriverAllocationList() {
		return driverAllocationList;
	}

	public void setDriverAllocationList(List<DriverAllocation> driverAllocationList) {
		this.driverAllocationList = driverAllocationList;
	}
	
	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}	

	
	public DriverAddress getGaragedAddress() {
		Iterator<DriverAddress> driverAddresses = driverAddressList.iterator();
		DriverAddress address;
		while (driverAddresses.hasNext()) {
			address = driverAddresses.next();
			if (address.getAddressType().getAddressType().equals("GARAGED")) {
				return address;
			}
		}
    	return null;
    }

	/**
	 * Helper method that retrieves a list of secondary/child drivers where
	 * this driver is the primary/parent used in saving drivers that are 
	 * related to the one being updated.
	 * @return List of drivers
	 */
	public List<Driver> getRelatedDrivers(){
		List<Driver> relatedDrivers = new ArrayList<Driver>();
		for(DriverRelationship dr : this.getChildRelationships())
			relatedDrivers.add(dr.getSecondaryDriver());
		return relatedDrivers;
	}

	/**
	 * Helper method that retrieves a list of primary drivers that this driver
	 * may be related as a descendant.
	 * @return List of drivers
	 */
	public List<Driver> getParentDrivers(){
		List<Driver> parentDrivers = null;
		if(this.getParentRelationships() != null && this.getParentRelationships().size() > 0)
			parentDrivers = new ArrayList<Driver>();
			for(DriverRelationship relationship : this.getParentRelationships())
				parentDrivers.add(relationship.getPrimaryDriver());
		return parentDrivers;
	}
	
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Driver)) {
            return false;
        }
        Driver other = (Driver) object;
        if ((this.drvId == null && other.drvId != null) || (this.drvId != null && !this.drvId.equals(other.drvId))) {
            return false;
        }
        return true;
    }
    
	@Override
    public String toString() {
        return "com.mikealbert.entity.Drivers[ drvId=" + drvId + " ]";
    }

	public String getPoolManager() {
		return poolManager;
	}

	public void setPoolManager(String poolManager) {
		this.poolManager = poolManager;
	}

	public List<DriverCostCenter> getDriverCostCenterList() {
		return driverCostCenterList;
	}

	public void setDriverCostCenterList(List<DriverCostCenter> driverCostCenterList) {
		this.driverCostCenterList = driverCostCenterList;
	}

	/** 
	 * This is a helper method that return the cost center that has no effective date (the one that has not been terminated)
	 * If a NONE cost centre has been set it will be returned; it should be handled to displaying it as the "Null" entry in the View/UI Layer so that it can be terminated
	 * appropriately per the business rules of the consuming system
	 */
	public DriverCostCenter getDriverCurrentCostCenter() {
	    if(getDriverCostCenterList() != null){
			for (DriverCostCenter driverCostCenter : getDriverCostCenterList()) {
		    	if(driverCostCenter.getEffectiveToDate() == null) {
		    		
		    		return driverCostCenter;			    		
		    	}
			}
	    }  
		return null;
	}

	public List<DriverRelationship> getChildRelationships() {
		return childRelationships;
	}

	public void setChildRelationships(List<DriverRelationship> childRelationships) {
		this.childRelationships = childRelationships;
	}

	public List<DriverRelationship> getParentRelationships() {
		return parentRelationships;
	}

	public void setParentRelationships(List<DriverRelationship> parentRelationships) {
		this.parentRelationships = parentRelationships;
	}	
	 
    public String getActiveInd() {
		return activeInd;
	}

	public void setActiveInd(String activeInd) {
		this.activeInd = activeInd;
	}
	
	public Long getCostCentreAccountCorporateId() {
		return costCentreAccountCorporateId;
	}

	public void setCostCentreAccountCorporateId(
			Long costCentreAccountCorporateId) {
		this.costCentreAccountCorporateId = costCentreAccountCorporateId;
	}

	public String getCostCentreAccountCode() {
		return costCentreAccountCode;
	}

	public void setCostCentreAccountCode(String costCentreAccountCode) {
		this.costCentreAccountCode = costCentreAccountCode;
	}

	public String getCostCentreAccountType() {
		return costCentreAccountType;
	}

	public void setCostCentreAccountType(String costCentreAccountType) {
		this.costCentreAccountType = costCentreAccountType;
	}

}

