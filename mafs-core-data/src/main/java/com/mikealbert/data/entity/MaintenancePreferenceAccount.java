package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to MAINT_PREF_ACCT table
 */

@Entity
@Table(name = "MAINT_PREF_ACCT")
public class MaintenancePreferenceAccount extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
	@Column(name="MPA_ID")
	private Long mpaId;
	
	@Column(name="ACCT_SPEC_INSTR")
	private String acctSpecInstr;
	
	@Column(name="REVISION_DATE")
	private Date revisionDate;
	
	@Column(name="ACTIVE_FROM_DATE")
	private Date activeFromDate;
	
	@Column(name="ACTIVE_TO_DATE")	
	private Date activeToDate;
	
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccount;
    
    @Column(name="CST_CST_ID")	
    private Long clientScheduleId;
    
    @Column(name="CLIENT_DEDICATED_PHONE")
	private String clientDedicatedPhone;
    
    @Column(name="CLIENT_DRIVER_INSTRUCTIONS")
    private String clientDriverInstructions;

	public Long getMpaId() {
		return mpaId;
	}

	public void setMpaId(Long mpaId) {
		this.mpaId = mpaId;
	}

	public String getAcctSpecInstr() {
		return acctSpecInstr;
	}

	public void setAcctSpecInstr(String acctSpecInstr) {
		this.acctSpecInstr = acctSpecInstr;
	}

	public Date getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Date getActiveFromDate() {
		return activeFromDate;
	}

	public void setActiveFromDate(Date activeFromDate) {
		this.activeFromDate = activeFromDate;
	}

	public Date getActiveToDate() {
		return activeToDate;
	}

	public void setActiveToDate(Date activeToDate) {
		this.activeToDate = activeToDate;
	}

	public Long getClientScheduleId() {
		return clientScheduleId;
	}

	public void setClientScheduleId(Long clientScheduleId) {
		this.clientScheduleId = clientScheduleId;
	}

	public String getClientDriverInstructions() {
		return clientDriverInstructions;
	}

	public void setClientDriverInstructions(String clientDriverInstructions) {
		this.clientDriverInstructions = clientDriverInstructions;
	}

	public String getClientDedicatedPhone() {
		return clientDedicatedPhone;
	}

	public void setClientDedicatedPhone(String clientDedicatedPhone) {
		this.clientDedicatedPhone = clientDedicatedPhone;
	}	
	
	
}
