package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PROGRESS_TYPE_CODES database table.
 * 
 */
@Entity
@Table(name="PROGRESS_TYPE_CODES")
public class ProgressTypeCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PROGRESS_TYPE")
	private String progressType;

	private String description;

	@Column(name="SYS_GENERATED_IND")
	private String sysGenerated;

    public ProgressTypeCode() {
    }

	public String getProgressType() {
		return this.progressType;
	}

	public void setProgressType(String progressType) {
		this.progressType = progressType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSysGenerated() {
		return sysGenerated;
	}

	public void setSysGenerated(String sysGenerated) {
		this.sysGenerated = sysGenerated;
	}
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.ProgressTypeCode[ progressType=" + progressType + " ]";
    }
}