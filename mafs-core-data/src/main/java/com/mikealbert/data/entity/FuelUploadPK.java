package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FuelUploadPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name="LOAD_ID")
	private Long loadId;	
	@Column(name="LOAD_SEQ")
	private Long loadSeq;
	@Column(name="LOAD_STATUS")
	private String loadStatus;
	public Long getLoadId() {
		return loadId;
	}
	public void setLoadId(Long loadId) {
		this.loadId = loadId;
	}
	public Long getLoadSeq() {
		return loadSeq;
	}
	public void setLoadSeq(Long loadSeq) {
		this.loadSeq = loadSeq;
	}
	public String getLoadStatus() {
		return loadStatus;
	}
	public void setLoadStatus(String loadStatus) {
		this.loadStatus = loadStatus;
	}
	
	
}
