package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
/**
 * Mapped to DIARIES table
 */
@Entity
@Table(name = "DIARIES")
public class DiaryEntry extends BaseEntity implements Serializable {

	/**
	 * Entity representing a Diary Entry within the DAIRIES table
	 */
	private static final long serialVersionUID = 4776152301593650955L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DIARY_SEQ")    
    @SequenceGenerator(name="DIARY_SEQ", sequenceName="DIARY_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "DRY_ID")
    private Long dryId;
	
    @Basic(optional = false)
    @NotNull
    @Size(max = 80)   
    @Column(name = "DIARY_DESC")  
	private String description;

    @Size(max = 2000)   
    @Column(name = "DIARY_NOTE")
	private String note;
	
    @Basic(optional = false)
    @NotNull
    @Column(name = "DIARY_DATE")
    @Temporal(TemporalType.DATE)
	private Date entryDate;
	
    @Basic(optional = false)
    @NotNull
    @Size(max = 10)   
    @Column(name = "ENTRY_TYPE")
	private String entryType;
	
    @Size(max = 10)   
    @Column(name = "DIARY_CATEGORY")
	private String entryCategory;
	
    @Basic(optional = false)
    @NotNull
    @Size(max = 25)   
    @Column(name = "DIARY_USER_ID")
	private String enteredBy;
	
    @Column(name = "DIARY_ACTION_DATE")
    @Temporal(TemporalType.DATE)
	private Date actionDate;
	
    @Size(max = 25)   
    @Column(name = "DIARY_ACTION_BY")
	private String actionFor;
	
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1) 
    @Column(name = "DIARY_ACTION_COMPLETE")
	private String actionComplete = "N";

    @Column(name = "COMPLETED_DATE")
    @Temporal(TemporalType.DATE)
	private Date completedDate;
	
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true)
    private ExternalAccount externalAccount;
    
    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")
    @ManyToOne(optional = true)
    private Driver driver;
    
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = true)
    private FleetMaster fleetMaster;

    
    @JoinColumn(name = "MRQ_ID", referencedColumnName = "MRQ_ID")
    @ManyToOne(fetch=FetchType.LAZY, optional = true)
    private MaintenanceRequest maintenanceRequest;
    
    
	public Long getDryId() {
		return dryId;
	}

	public void setDryId(Long dryId) {
		this.dryId = dryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getEntryType() {
		return entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	public String getEntryCategory() {
		return entryCategory;
	}

	public void setEntryCategory(String entryCategory) {
		this.entryCategory = entryCategory;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getActionComplete() {
		return actionComplete;
	}

	public void setActionComplete(String actionComplete) {
		this.actionComplete = actionComplete;
	}

	public String getActionFor() {
		return actionFor;
	}

	public void setActionFor(String actionFor) {
		this.actionFor = actionFor;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}
	

}
