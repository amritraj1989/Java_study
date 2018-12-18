package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the SERVICE_TASKS database table.
 */
@Entity
@Table(name="SERVICE_TASKS")
public class ServiceTask extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5172359635639737461L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SRVT_SEQ")    
    @SequenceGenerator(name="SRVT_SEQ", sequenceName="SRVT_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "SRVT_ID")
    private Long srvtId;
        
    @JoinColumn(name = "MAINT_CAT_CODE", referencedColumnName = "MAINT_CAT_CODE")
    @ManyToOne
    private MaintenanceCategory maintenanceCategory;  
    
	@Column(name="SERVICE_CODE")
    private String serviceCode;
	
	@Column(name="TASK_DESCRIPTION")
    private String taskDescription;
	
	@Column(name="MAX_SERVICE_COST")
	private BigDecimal cost;
	
	@Column(name="ACTIVE_FLAG")
	private String activeFlag;
    
    
    public ServiceTask() {}

	public Long getSrvtId() {
		return srvtId;
	}

	public void setSrvtId(Long srvtId) {
		this.srvtId = srvtId;
	}

	public MaintenanceCategory getMaintenanceCategory() {
		return maintenanceCategory;
	}

	public void setMaintenanceCategory(MaintenanceCategory maintenanceCategory) {
		this.maintenanceCategory = maintenanceCategory;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.ServiceTask[ srvtId=" + this.getSrvtId() + " ]";
    }

}