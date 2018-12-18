package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ERROR_CODE")
public class ErrorCode implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name= "ERROR_CODE")
	private	Long	errorCode;
	@Column (name = "ERROR_DESCRIPTION")
	private String	errorDescription;
	public Long getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Long errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	

}
