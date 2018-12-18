package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Mapped to MAINT_REQUEST_USERS table
 * @author Raj
 */
@Entity
@Table(name = "MAINT_REQUEST_USERS")
public class MaintenanceRequestUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MRU_SEQ")    
    @SequenceGenerator(name="MRU_SEQ", sequenceName="MRU_SEQ", allocationSize=1)
    @Basic(optional = false)	
	@Column(name="MRU_ID")
	private long mruId;

	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="MRQ_MRQ_ID", referencedColumnName = "MRQ_ID")
	private MaintenanceRequest maintenanceRequest;

	@Column(name="NEW_COST")
	private BigDecimal newCost;

	@Column(name="OLD_COST")
	private BigDecimal oldCost;

    @Temporal( TemporalType.DATE)
	@Column(name="UPDATE_DATE")
    @Basic(optional = false)
	private Date updateDate;

	@Column(name="UPDATE_REASON")
	@Basic(optional = false)
	private String updateReason;

	@Column(name="USER_ID")
	@Basic(optional = false)
	private String userId;

    public MaintenanceRequestUser() {
    }

	public long getMruId() {
		return this.mruId;
	}

	public void setMruId(long mruId) {
		this.mruId = mruId;
	}


	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}

	public BigDecimal getNewCost() {
		return this.newCost;
	}

	public void setNewCost(BigDecimal newCost) {
		this.newCost = newCost;
	}

	public BigDecimal getOldCost() {
		return this.oldCost;
	}

	public void setOldCost(BigDecimal oldCost) {
		this.oldCost = oldCost;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateReason() {
		return this.updateReason;
	}

	public void setUpdateReason(String updateReason) {
		this.updateReason = updateReason;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
