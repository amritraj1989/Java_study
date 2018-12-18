package com.mikealbert.data.entity;

import java.io.Serializable;

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
 * The persistent class for the MASTER_INTERVAL_SERVICE_TASKS database table.
 */
@Entity
@Table(name="MASTER_INTERVAL_SERVICE_TASKS")
public class MasterScheduleInterval extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8367018154519217139L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MIST_SEQ")    
    @SequenceGenerator(name="MIST_SEQ", sequenceName="MIST_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "MIST_ID")
    private Long mistId;
     
    @JoinColumn(name = "MSCH_MSCH_ID", referencedColumnName = "MSCH_ID")
    @ManyToOne(optional = false)
    private MasterSchedule masterSchedule; 
	
    @JoinColumn(name = "SRVT_SRVT_ID", referencedColumnName = "SRVT_ID")
    @ManyToOne(optional = false)
    private ServiceTask serviceTask; 
	
	@Column(name="MIST_INT")
	private Integer intervalMultiple;
	
	@Column(name="PRINT_ORDER")
	private Integer order;

	@Column(name="REPEAT_FLAG")
	private String repeatFlag;

	
	public MasterScheduleInterval() {}
	
	
	public Long getMistId() {
		return mistId;
	}

	public void setMistId(Long mistId) {
		this.mistId = mistId;
	}

	public MasterSchedule getMasterSchedule() {
		return masterSchedule;
	}

	public void setMasterSchedule(MasterSchedule masterSchedule) {
		this.masterSchedule = masterSchedule;
	}

	public ServiceTask getServiceTask() {
		return serviceTask;
	}

	public void setServiceTask(ServiceTask serviceTask) {
		this.serviceTask = serviceTask;
	}

	public Integer getIntervalMultiple() {
		return intervalMultiple;
	}

	public void setInterval(Integer intervalMultiple) {
		this.intervalMultiple = intervalMultiple;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getRepeatFlag() {
		return repeatFlag;
	}

	public void setRepeatFlag(String repeatFlag) {
		this.repeatFlag = repeatFlag;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MasterScheduleInterval[ mistId=" + this.getMistId() + " ]";
    }



}