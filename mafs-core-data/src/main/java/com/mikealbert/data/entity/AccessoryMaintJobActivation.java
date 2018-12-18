package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ACCESSORY_MAINT_JOB_ACTIVATION")
public class AccessoryMaintJobActivation extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AccessoryMaintJobActivationPK id;
	
	@Column(name = "START_DATE", nullable = true)
	private Date startDate;
	
	@Column(name = "REVISION_DATE", nullable = true)
	private Date revisionDate;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "SUP_SUP_ID", referencedColumnName = "SUP_ID")
	private ServiceProvider serviceProvider;
	
	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name = "MRQ_MRQ_ID", referencedColumnName = "MRQ_ID")
	private MaintenanceRequest maintenanceRequest;

	public AccessoryMaintJobActivationPK getId() {
		return id;
	}

	public void setId(AccessoryMaintJobActivationPK id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}
		
}
