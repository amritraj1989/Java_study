package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the CLIENT_SCHEUDLE_TYPE database table.
 * 
 */
@Entity
@Table(name="CLIENT_SCHEDULE_TYPES")
public class ClientScheduleType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CST_ID")
	private Long cstId;

	@Column(name="SCHEDULE_TYPE")
	private String scheduleType;

	@Column(name="SCHEDULE_DESCRIPTION")
	private String scheduleDescription;

    public ClientScheduleType() {
    }

	public Long getCstId() {
		return this.cstId;
	}

	public void setCstId(Long cstId) {
		this.cstId = cstId;
	}

	public String getScheduleType() {
		return this.scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getScheduleDescription() {
		return this.scheduleDescription;
	}

	public void setScheduleDescription(String scheduleDescription) {
		this.scheduleDescription = scheduleDescription;
	}
}