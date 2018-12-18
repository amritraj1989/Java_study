package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mikealbert.util.MALUtilities;

/**
 * Maintenance Rules
 * The persistent class for the MAINT_SCHEDULE_RULES database table.  
 * The rules that attach the vehicle to the master schedule
 */
@Entity
@Table(name="MAINT_SCHEDULE_RULES")
public class MaintScheduleRule extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5165606713700949869L;

	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MSC_ID_SEQ")    
    @SequenceGenerator(name="MSC_ID_SEQ", sequenceName="MSC_ID_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "MSC_ID")
    private Long mscId;     
	 
    @Column(name="YEAR")
    private String year;
    
    @Column(name="MAKE_CODE")
    private String makeCode;
        
    @Column(name="MAKE_MODEL_DESC")
    private String makeModelDesc;

    @Column(name="VARIENT_DESC")
    private String variantDesc;

    @Column(name="MODEL_TYPE_DESC")
    private String modelTypeDesc;

    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne
    private ExternalAccount scheduleAccount;		

    @Column(name="DUTY")
	private String duty;
    
    
    @Column(name="FUEL_TYPE_GROUP")
	private String fuelTypeGroup;

    @Column(name="DRIVE_TRAIN_GROUP")
	private String driveTrainGroup;
	
    @Column(name="BASE_SCHEDULE")
	private String baseSchedule;
    
    @JoinColumn(name = "MSCH_MSCH_ID", referencedColumnName = "MSCH_ID")
    @ManyToOne
    private MasterSchedule masterSchedule;
	
	@Column(name="ACTIVE_FLAG")
	private String activeFlag;
	
	@Column(name="HIGH_MILEAGE")
	private String highMileage;
	
	
    
	public MaintScheduleRule() {}
	
	public Long getMscId() {
		return mscId;
	}

	public void setMscId(Long mscId) {
		this.mscId = mscId;
	}	

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	
	public String getMakeCode() {
		return makeCode;
	}

	public void setMakeCode(String makeCode) {
		this.makeCode = makeCode;
	}
	
	
	public String getMakeModelDesc() {
		return makeModelDesc;
	}

	public void setMakeModelDesc(String makeModelDesc) {
		this.makeModelDesc = makeModelDesc;
	}
	
	
	public String getModelTypeDesc() {
		return modelTypeDesc;
	}

	public void setModelTypeDesc(String modelTypeDesc) {
		this.modelTypeDesc = modelTypeDesc;
	}

	public String getVariantDesc() {
		return variantDesc;
	}

	public void setVariantDesc(String variantDesc) {
		this.variantDesc = variantDesc;
	}
	
	
	public ExternalAccount getScheduleAccount() {
		return scheduleAccount;
	}

	public void setScheduleAccount(ExternalAccount scheduleAccount) {
		this.scheduleAccount = scheduleAccount;
	}
	
	
	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}
	
	
	public String getFuelTypeGroup() {
		return fuelTypeGroup;
	}

	public void setFuelTypeGroup(String fuelTypeGroup) {
		this.fuelTypeGroup = fuelTypeGroup;
	}
	
	public String getDriveTrainGroup() {
		return driveTrainGroup;
	}

	public void setDriveTrainGroup(String driveTrainGroup) {
		this.driveTrainGroup = driveTrainGroup;
	}
	
	
	public String getBaseSchedule() {
		return baseSchedule;
	}

	public void setBaseSchedule(String baseSchedule) {
		this.baseSchedule = baseSchedule;
	}
	
	
	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	

	public MasterSchedule getMasterSchedule() {
		return masterSchedule;
	}

	public void setMasterSchedule(MasterSchedule masterSchedule) {
		this.masterSchedule = masterSchedule;
	}
	
	

	public String getHighMileage() {
		if(MALUtilities.isEmpty(highMileage))
			return "N";
		return highMileage;
	}

	public void setHighMileage(String highMileage) {
		this.highMileage = highMileage;
	}
	

	
	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintScheduleCategory[ mscId=" + this.getMscId() + " ]";
    }


}