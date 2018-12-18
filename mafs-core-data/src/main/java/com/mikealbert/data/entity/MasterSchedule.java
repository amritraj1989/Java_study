package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

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
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the MASTER_SCHEDULES database table.
 */
@Entity
@Table(name="MASTER_SCHEDULES")
public class MasterSchedule extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8367018184519217139L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MSCH_SEQ")    
    @SequenceGenerator(name="MSCH_SEQ", sequenceName="MSCH_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "MSCH_ID")
    private Long mschId;
     
	
    @JoinColumn(name = "CST_CST_ID", referencedColumnName = "CST_ID")
    @ManyToOne
    private ClientScheduleType clientScheduleType;

    @OneToMany(mappedBy = "masterSchedule", fetch = FetchType.EAGER, cascade = CascadeType.ALL ,orphanRemoval = true)
    @javax.persistence.OrderBy("order ASC")
    private List<MasterScheduleInterval> masterScheduleIntervals;
    
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne
    private ExternalAccount clientAccount;
    
	@Column(name="MASTER_CODE")
	private String masterCode;  
	
	@Column(name="DESCRIPTION")
    private String description;
	
	@Column(name="DISTANCE_FREQUENCY")
	private int distanceFrequency;
	
	@Column(name="MONTH_FREQUENCY")
	private int monthFrequency;
	
	@Column(name="HIDDEN_FLAG")
	private String hiddenFlag;
	

	public MasterSchedule() {}

	public Long getMschId() {
		return mschId;
	}

	public void setMschId(Long mschId) {
		this.mschId = mschId;
	}	
	
	public ClientScheduleType getClientScheduleType() {
		return clientScheduleType;
	}
	
	public void setClientScheduleType(ClientScheduleType clientScheduleType) {
		this.clientScheduleType = clientScheduleType;
	}
	
	public String getMasterCode() {
		return masterCode;
	}

	public void setMasterCode(String masterCode) {
		this.masterCode = masterCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDistanceFrequency() {
		return distanceFrequency;
	}

	public void setDistanceFrequency(int distanceFrequency) {
		this.distanceFrequency = distanceFrequency;
	}
	
	public int getMonthFrequency() {
		return monthFrequency;
	}

	public void setMonthFrequency(int monthFrequency) {
		this.monthFrequency = monthFrequency;
	}

	public String getHiddenFlag() {
		return hiddenFlag;
	}

	public void setHiddenFlag(String hiddenFlag) {
		this.hiddenFlag = hiddenFlag;
	}
	
	public List<MasterScheduleInterval> getMasterScheduleIntervals() {
		return masterScheduleIntervals;
	}

	public void setMasterScheduleIntervals(
			List<MasterScheduleInterval> masterScheduleIntervals) {
		this.masterScheduleIntervals = masterScheduleIntervals;
	}
	
	

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MasterSchedule[ mschId=" + this.getMschId() + " ]";
    }

}