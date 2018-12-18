package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to TRIM_CODES table
 */
@Entity
@Table(name = "TRIM_CODES")
public class TrimCodes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -918596737126980612L;

	@Id
	@Column(name = "TRC_ID")
	private Long trcId;
	
	@Column(name = "TRIM_CODE")
	private String trimCode;
	
	@Column(name="TRIM_DESC")
	private String trimDescription;
	
	@Column(name="NEW_TRIM_CODE")
	private String newTrimCode;

	public Long getTrcId() {
		return trcId;
	}

	public void setTrcId(Long trcId) {
		this.trcId = trcId;
	}

	public String getTrimCode() {
		return trimCode;
	}

	public void setTrimCode(String trimCode) {
		this.trimCode = trimCode;
	}

	public String getTrimDescription() {
		return trimDescription;
	}

	public void setTrimDescription(String trimDescription) {
		this.trimDescription = trimDescription;
	}

	public String getNewTrimCode() {
		return newTrimCode;
	}

	public void setNewTrimCode(String newTrimCode) {
		this.newTrimCode = newTrimCode;
	}
	
}
