package com.mikealbert.data.vo;

import java.util.Date;

public class TALFileNoteVO {

	private Long dryId;
	private Long fmsId;	
	private String description;
	private String detail;
	private String entryUser;
	private Date entryDate;	
	
	public TALFileNoteVO(){}

	public Long getDryId() {
		return dryId;
	}

	public void setDryId(Long dryId) {
		this.dryId = dryId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getEntryUser() {
		return entryUser;
	}

	public void setEntryUser(String entryUser) {
		this.entryUser = entryUser;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	
}
