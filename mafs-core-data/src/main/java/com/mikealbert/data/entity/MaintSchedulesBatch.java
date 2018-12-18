package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "MAINT_SCHEDULES_BATCH")
public class MaintSchedulesBatch extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	/*@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BATCH_ID_SEQ")    
    @SequenceGenerator(name="BATCH_ID_SEQ", sequenceName="BATCH_ID_SEQ", allocationSize=1)*/	
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Column(name="BATCH_NAME" , nullable= false, length=25)
	private	String	batchName;
	
	@Column(name="DESCRIPTION" , length=240)
	private	String	description;
	
	@Column(name="STATUS" , nullable= false, length=1)
	private	String	status;
	
	@Column(name= "COMPLETION_DATE")
	private	Date	completionDate;

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	
	
}
